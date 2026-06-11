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
    private TextView tvDisplay;

    private int lives = MAX_LIVES;
    private int score = 0;
    private int correctAnswer;
    private final StringBuilder currentInput = new StringBuilder();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new DatabaseHelper(this);

        tvOperation = findViewById(R.id.tv_operation);
        tvLives     = findViewById(R.id.tv_lives);
        tvScore     = findViewById(R.id.tv_score);
        tvDisplay   = findViewById(R.id.tv_display);

        // Menu button → ends the current game (save score flow)
        findViewById(R.id.btn_menu).setOnClickListener(v -> showGameOverDialog());

        // Digit buttons
        int[] digitIds = {
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
            R.id.btn_8, R.id.btn_9
        };
        for (int i = 0; i < digitIds.length; i++) {
            final String digit = String.valueOf(i);
            findViewById(digitIds[i]).setOnClickListener(v -> appendDigit(digit));
        }

        findViewById(R.id.btn_delete).setOnClickListener(v -> deleteLastDigit());
        findViewById(R.id.btn_validate).setOnClickListener(v -> checkAnswer());

        generateOperation();
        updateStatusBar();
    }

    private void appendDigit(String digit) {
        if (currentInput.length() < 5) {
            currentInput.append(digit);
            refreshDisplay();
        }
    }

    private void deleteLastDigit() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            refreshDisplay();
        }
    }

    private void refreshDisplay() {
        tvDisplay.setText(currentInput.length() == 0 ? "?" : currentInput.toString());
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
                symbol = "−";
                break;
            case 2: // Multiplication
                a = random.nextInt(10) + 1;
                b = random.nextInt(10) + 1;
                correctAnswer = a * b;
                symbol = "×";
                break;
            default: // Division — guaranteed integer result
                b = random.nextInt(9) + 2;
                correctAnswer = random.nextInt(10) + 1;
                a = b * correctAnswer;
                symbol = "÷";
                break;
        }

        tvOperation.setText(a + " " + symbol + " " + b + " = ?");
        currentInput.setLength(0);
        refreshDisplay();
    }

    private void checkAnswer() {
        if (currentInput.length() == 0) return;

        int userAnswer;
        try {
            userAnswer = Integer.parseInt(currentInput.toString());
        } catch (NumberFormatException e) {
            return;
        }

        if (userAnswer == correctAnswer) {
            score++;
            Toast.makeText(this, getString(R.string.correct), Toast.LENGTH_SHORT).show();
            generateOperation();
            updateStatusBar();
        } else {
            lives--;
            Toast.makeText(this, getString(R.string.wrong) + correctAnswer, Toast.LENGTH_SHORT).show();
            if (lives <= 0) {
                showGameOverDialog();
            } else {
                generateOperation();
                updateStatusBar();
            }
        }
    }

    private void updateStatusBar() {
        tvLives.setText(buildHearts(lives));
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
                    startActivity(new Intent(this, HighscoreActivity.class));
                    finish();
                })
                .setNegativeButton(getString(R.string.skip), (dialog, which) -> finish())
                .show();
    }
}
