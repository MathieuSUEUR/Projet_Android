package com.mathieusueur.projetandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final int MAX_LIVES = 3;
    private static final int MAX_NUMBER = 20;

    private TextView tvOperation;
    private TextView tvLives;
    private TextView tvScore;
    private EditText etAnswer;

    private int lives = MAX_LIVES;
    private int score = 0;
    private int correctAnswer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new DatabaseHelper(this);

        tvOperation = findViewById(R.id.tv_operation);
        tvLives = findViewById(R.id.tv_lives);
        tvScore = findViewById(R.id.tv_score);
        etAnswer = findViewById(R.id.et_answer);

        Button btnSubmit = findViewById(R.id.btn_submit);
        Button btnMenu = findViewById(R.id.btn_menu);

        btnSubmit.setOnClickListener(v -> checkAnswer());
        btnMenu.setOnClickListener(v -> finish());

        etAnswer.setOnEditorActionListener((v, actionId, event) -> {
            checkAnswer();
            return true;
        });

        generateOperation();
        updateStatusBar();
    }

    private void generateOperation() {
        Random random = new Random();
        int a, b;
        String symbol;

        switch (random.nextInt(4)) {
            case 0: // Addition
                a = random.nextInt(MAX_NUMBER) + 1;
                b = random.nextInt(MAX_NUMBER) + 1;
                correctAnswer = a + b;
                symbol = "+";
                break;
            case 1: // Subtraction — result always >= 0
                a = random.nextInt(MAX_NUMBER) + 1;
                b = random.nextInt(a) + 1;
                correctAnswer = a - b;
                symbol = "-";
                break;
            case 2: // Multiplication (smaller numbers to keep it manageable)
                a = random.nextInt(10) + 1;
                b = random.nextInt(10) + 1;
                correctAnswer = a * b;
                symbol = "×";
                break;
            default: // Division — build from quotient to guarantee integer result
                b = random.nextInt(9) + 2;           // divisor 2–10
                correctAnswer = random.nextInt(10) + 1; // quotient 1–10
                a = b * correctAnswer;
                symbol = "÷";
                break;
        }

        tvOperation.setText(a + " " + symbol + " " + b + " = ?");
    }

    private void checkAnswer() {
        String input = etAnswer.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_answer), Toast.LENGTH_SHORT).show();
            return;
        }

        int userAnswer;
        try {
            userAnswer = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            return;
        }

        etAnswer.setText("");

        if (userAnswer == correctAnswer) {
            score++;
            Toast.makeText(this, getString(R.string.correct), Toast.LENGTH_SHORT).show();
        } else {
            lives--;
            Toast.makeText(this,
                    getString(R.string.wrong) + correctAnswer,
                    Toast.LENGTH_SHORT).show();

            if (lives <= 0) {
                showGameOverDialog();
                return;
            }
        }

        generateOperation();
        updateStatusBar();
    }

    private void updateStatusBar() {
        tvLives.setText(getString(R.string.lives) + " " + buildHearts(lives));
        tvScore.setText(getString(R.string.score) + " " + score);
    }

    private String buildHearts(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_LIVES; i++) {
            sb.append(i < count ? "♥" : "♡");
            if (i < MAX_LIVES - 1) sb.append(" ");
        }
        return sb.toString();
    }

    private void showGameOverDialog() {
        EditText nameInput = new EditText(this);
        nameInput.setHint(getString(R.string.enter_name));

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.game_over))
                .setMessage(getString(R.string.game_over_message) + score)
                .setView(nameInput)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.save), (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (name.isEmpty()) name = getString(R.string.anonymous);
                    dbHelper.insertScore(name, score);
                    goToHighscores();
                })
                .setNegativeButton(getString(R.string.skip), (dialog, which) -> finish())
                .show();
    }

    private void goToHighscores() {
        startActivity(new Intent(this, HighscoreActivity.class));
        finish();
    }
}
