package com.example.quizmaths;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

// Écran qui affiche les 10 meilleurs scores.
// On peut choisir en haut entre le mode classique et le mode chronométré.
public class HighscoreActivity extends AppCompatActivity {

    private ListView listeScores;
    private Button boutonClassique;
    private Button boutonChrono;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        listeScores = findViewById(R.id.liste_scores);
        boutonClassique = findViewById(R.id.bouton_classique);
        boutonChrono = findViewById(R.id.bouton_chrono);
        Button boutonRetour = findViewById(R.id.bouton_retour);

        db = new DatabaseHelper(this);

        // Bouton "Classique" : affiche le classement du mode classique
        boutonClassique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherScores(DatabaseHelper.MODE_CLASSIQUE);
            }
        });

        // Bouton "Chronométré" : affiche le classement du mode chronométré
        boutonChrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherScores(DatabaseHelper.MODE_CHRONO);
            }
        });

        // Bouton retour vers le menu
        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Mode affiché par défaut : celui passé par le jeu, sinon classique
        String mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = DatabaseHelper.MODE_CLASSIQUE;
        }
        afficherScores(mode);
    }

    // Affiche le top 10 du mode choisi et met en évidence le bouton actif
    private void afficherScores(String mode) {
        List<String> scores = db.getTop10(mode);

        // S'il n'y a aucun score pour ce mode, on affiche un message
        if (scores.isEmpty()) {
            scores.add(getString(R.string.aucun_score));
        }

        ArrayAdapter<String> adaptateur = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, scores);
        listeScores.setAdapter(adaptateur);

        // On met en évidence le bouton du mode choisi (l'autre est atténué)
        boolean classique = mode.equals(DatabaseHelper.MODE_CLASSIQUE);
        boutonClassique.setAlpha(classique ? 1f : 0.5f);
        boutonChrono.setAlpha(classique ? 0.5f : 1f);
    }
}
