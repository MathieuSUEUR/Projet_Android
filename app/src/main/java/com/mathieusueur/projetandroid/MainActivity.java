package com.mathieusueur.projetandroid;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = findViewById(R.id.btn_play);
        Button btnHighscores = findViewById(R.id.btn_highscores);

        btnPlay.setOnClickListener(v ->
                startActivity(new Intent(this, GameActivity.class)));

        btnHighscores.setOnClickListener(v ->
                startActivity(new Intent(this, HighscoreActivity.class)));
    }
}
