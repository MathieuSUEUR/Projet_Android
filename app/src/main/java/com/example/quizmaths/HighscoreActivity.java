package com.example.quizmaths;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

// Écran qui affiche les 10 meilleurs scores enregistrés.
public class HighscoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        ListView listeScores = findViewById(R.id.liste_scores);
        Button boutonRetour = findViewById(R.id.bouton_retour);

        // On lit les meilleurs scores dans la base SQLite
        DatabaseHelper db = new DatabaseHelper(this);
        List<String> scores = db.getTop10();

        // S'il n'y a aucun score, on affiche un message
        if (scores.isEmpty()) {
            scores.add(getString(R.string.aucun_score));
        }

        // Affichage des scores dans la liste
        ArrayAdapter<String> adaptateur = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, scores);
        listeScores.setAdapter(adaptateur);

        // Le bouton retour ferme l'écran et revient au menu
        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
