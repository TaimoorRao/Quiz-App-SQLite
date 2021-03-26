package com.example.task17;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighScore";

    private TextView textViewHighScore;
    private Spinner spinnerDifficulty;
    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHighScore = findViewById(R.id.text_view_highscore);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);

        String[] difficultyLevels = Question.getAllDifficultyLevels();
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String >(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);

        loadHighScore();

        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    private void startQuiz() {
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);
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

    @SuppressLint("SetTextI18n")
    private void loadHighScore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGHSCORE, 0);
        textViewHighScore.setText("High Score: " + highScore);
    }

    @SuppressLint("SetTextI18n")
    private void updateHighScore(int highScoreNew) {
        highScore = highScoreNew;
        textViewHighScore.setText("High Score: " + highScore);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highScore);
        editor.apply();
    }
}