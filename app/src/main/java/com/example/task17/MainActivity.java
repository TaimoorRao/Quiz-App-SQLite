package com.example.task17;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.task17.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighScore";

    private int highScore;

    private ActivityMainBinding bindingMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setMainLayoutValues(bindingMain);

        loadCategories();
        loadDifficultyLevels();
        loadHighScore();

        bindingMain.buttonStartQuiz.setOnClickListener(v -> startQuiz());
    }

    private void setMainLayoutValues(ActivityMainBinding bindingMain) {
        bindingMain.setMyQuiz("My Quiz");
        bindingMain.setHighscore("High Score: 0");
        bindingMain.setBtnStart("Start Quiz");
    }

    private void startQuiz() {
        Category selectedCategory = (Category) bindingMain.spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();
        String difficulty = bindingMain.spinnerDifficulty.getSelectedItem().toString();

        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);

        // Here you can code for adding category or question from user-end

        // Category or categories list
        // Question or questions list

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QUIZ) {
            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
                if (score > highScore) {
                    updateHighScore(score);
                }
            }
        }
    }

    /**
     * Here we get Categories from Database
     */
    private void loadCategories() {
        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        List<Category> categories = dbHelper.getAllCategories();

        ArrayAdapter<Category> adapterCategory = new ArrayAdapter<Category>(this,
                android.R.layout.simple_spinner_item, categories);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bindingMain.spinnerCategory.setAdapter(adapterCategory);
    }

    /**
     * We get difficulty levels from Question Class
     */
    private void loadDifficultyLevels() {
        String[] difficultyLevels = Question.getAllDifficultyLevels();

        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bindingMain.spinnerDifficulty.setAdapter(adapterDifficulty);
    }

    @SuppressLint("SetTextI18n")
    private void loadHighScore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGHSCORE, 0);
        bindingMain.setHighscore("High Score: " + highScore);
    }

    @SuppressLint("SetTextI18n")
    private void updateHighScore(int highScoreNew) {
        highScore = highScoreNew;
        bindingMain.setHighscore("High Score: " + highScore);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highScore);
        editor.apply();
    }
}