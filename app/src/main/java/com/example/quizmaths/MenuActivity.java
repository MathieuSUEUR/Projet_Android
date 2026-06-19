package com.example.quizmaths;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// Écran d'accueil de l'application.
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button boutonScores = findViewById(R.id.bouton_scores);

        // Le bouton "Meilleurs scores" ouvre l'écran des scores
        boutonScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, HighscoreActivity.class));
            }
        });
    }
}
