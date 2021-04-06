package com.example.task17;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.task17.databinding.ActivityQuizBindingImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;

    private ActivityQuizBindingImpl bindingQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingQuiz = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        setValue(bindingQuiz);

        textColorDefaultRb = bindingQuiz.radioButton1.getTextColors();
        textColorDefaultCd = bindingQuiz.textViewCountdown.getTextColors();

        Intent intent = getIntent();
        int categoryID = intent.getIntExtra(MainActivity.EXTRA_CATEGORY_ID, 0);
//        String categoryName = intent.getStringExtra(MainActivity.EXTRA_CATEGORY_NAME);
        String difficulty = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);

        bindingQuiz.setDifficulty("Difficulty: " + difficulty);
        bindingQuiz.setCategory("Category: " + categoryID);

        if (savedInstanceState == null) {
            QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
            questionList = dbHelper.getQuestions(categoryID, difficulty);
            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);

            showNextQuestion();
        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered){
                startCountDown();
            } else {
                updateCountDownText();
                showSolution();
            }
        }

        RadioButton rb1 = bindingQuiz.radioButton1;
        RadioButton rb2 = bindingQuiz.radioButton2;
        RadioButton rb3 = bindingQuiz.radioButton3;
        bindingQuiz.buttonConfirmNext.setOnClickListener(v -> {
            if (!answered) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                    checkAnswer();
                } else {
                    Toast.makeText(QuizActivity.this, "Please select option", Toast.LENGTH_SHORT).show();
                }
            } else {
                showNextQuestion();
            }
        });
    }

    private void setValue(ActivityQuizBindingImpl bindingQuiz) {
        bindingQuiz.setScore("Score: 0");
        bindingQuiz.setQuestionCount("Question: 1/x");
        bindingQuiz.setDifficulty("Difficulty: x");
        bindingQuiz.setCategory("Category: x");
        bindingQuiz.setTimer("00:30");
        bindingQuiz.setQuestion("Here will be the question");
        bindingQuiz.setRadiob1("Option 1");
        bindingQuiz.setRadiob2("Option 2");
        bindingQuiz.setRadiob3("Option 3");
        bindingQuiz.setButton("Confirm");
    }

    private void showNextQuestion() {
        bindingQuiz.radioButton1.setTextColor(textColorDefaultRb);
        bindingQuiz.radioButton2.setTextColor(textColorDefaultRb);
        bindingQuiz.radioButton3.setTextColor(textColorDefaultRb);
        bindingQuiz.radioGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);
            bindingQuiz.setQuestion(currentQuestion.getQuestion());
            bindingQuiz.setRadiob1(currentQuestion.getOption1());
            bindingQuiz.setRadiob2(currentQuestion.getOption2());
            bindingQuiz.setRadiob3(currentQuestion.getOption3());
            questionCounter++;
            bindingQuiz.setQuestionCount("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            bindingQuiz.setButton("Confirm");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        } else {
            finishQuiz();
        }
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        bindingQuiz.setTimer(timeFormatted);
        if (timeLeftInMillis < 10000) {
            bindingQuiz.textViewCountdown.setTextColor(Color.RED);
        } else {
            bindingQuiz.textViewCountdown.setTextColor(textColorDefaultCd);
        }
    }

    private void checkAnswer() {
        RadioGroup rbGroup = bindingQuiz.radioGroup;
        answered = true;
        /**
         * TODO
         */
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;
        if (answerNr == currentQuestion.getAnswerNr()) {
            score++;
            bindingQuiz.setScore("Score: " + score);
        }
        showSolution();
    }

    private void showSolution() {
        RadioButton rb1 = bindingQuiz.radioButton1;
        RadioButton rb2 = bindingQuiz.radioButton2;
        RadioButton rb3 = bindingQuiz.radioButton3;

        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        bindingQuiz.radioGroup.clearCheck();

        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        switch (currentQuestion.getAnswerNr()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                bindingQuiz.setQuestion("Answer 1 is correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                bindingQuiz.setQuestion("Answer 2 is correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                bindingQuiz.setQuestion("Answer 3 is correct");
                break;
        }
        if (questionCounter < questionCountTotal) {
            bindingQuiz.setButton("Next");
        } else {
            bindingQuiz.setButton("Finish");
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}