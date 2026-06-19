package com.example.quizmaths;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

// Écran de jeu : on pose des calculs au joueur.
// Le joueur a 3 vies et gagne 10 points par bonne réponse.
public class JeuActivity extends AppCompatActivity {

    private int vies = 3;
    private int score = 0;
    private int bonneReponse; // résultat attendu de la question en cours

    private TextView texteInfos;
    private TextView texteQuestion;
    private EditText champReponse;

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);

        texteInfos = findViewById(R.id.texte_infos);
        texteQuestion = findViewById(R.id.texte_question);
        champReponse = findViewById(R.id.champ_reponse);
        Button boutonValider = findViewById(R.id.bouton_valider);

        // Le bouton "Valider" vérifie la réponse du joueur
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valider();
            }
        });

        // On affiche la première question
        nouvelleQuestion();
    }

    // Génère une nouvelle question avec une opération aléatoire
    private void nouvelleQuestion() {
        int operateur = random.nextInt(4); // 0=+, 1=-, 2=*, 3=/
        int a, b;
        String signe;

        switch (operateur) {
            case 0: // addition
                a = random.nextInt(50) + 1;
                b = random.nextInt(50) + 1;
                bonneReponse = a + b;
                signe = "+";
                break;
            case 1: // soustraction (a >= b pour éviter les nombres négatifs)
                a = random.nextInt(50) + 1;
                b = random.nextInt(a) + 1;
                bonneReponse = a - b;
                signe = "-";
                break;
            case 2: // multiplication
                a = random.nextInt(10) + 1;
                b = random.nextInt(10) + 1;
                bonneReponse = a * b;
                signe = "×";
                break;
            default: // division entière : on construit a pour que a % b == 0
                b = random.nextInt(9) + 1;            // diviseur entre 1 et 9
                bonneReponse = random.nextInt(9) + 1; // quotient entre 1 et 9
                a = b * bonneReponse;                 // ainsi a / b est entier
                signe = "÷";
                break;
        }

        texteQuestion.setText(a + " " + signe + " " + b + " = ?");
        champReponse.setText("");
        majInfos();
    }

    // Met à jour l'affichage du score et des vies
    private void majInfos() {
        texteInfos.setText(getString(R.string.score_vies, score, vies));
    }

    // Vérifie la réponse saisie par le joueur
    private void valider() {
        String saisie = champReponse.getText().toString().trim();
        if (saisie.isEmpty()) {
            Toast.makeText(this, R.string.entrez_reponse, Toast.LENGTH_SHORT).show();
            return;
        }

        int reponse = Integer.parseInt(saisie);
        if (reponse == bonneReponse) {
            score += 10;
            Toast.makeText(this, R.string.bonne_reponse, Toast.LENGTH_SHORT).show();
        } else {
            vies--;
            Toast.makeText(this, R.string.mauvaise_reponse, Toast.LENGTH_SHORT).show();
        }

        // Plus de vies : la partie est terminée
        if (vies <= 0) {
            finDePartie();
        } else {
            nouvelleQuestion();
        }
    }

    // Fin de partie : on demande le nom du joueur et on enregistre le score
    private void finDePartie() {
        final EditText champNom = new EditText(this);
        champNom.setHint(R.string.votre_nom);

        new AlertDialog.Builder(this)
                .setTitle(R.string.partie_terminee)
                .setMessage(getString(R.string.score_final, score))
                .setView(champNom)
                .setCancelable(false)
                .setPositiveButton(R.string.valider, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nom = champNom.getText().toString().trim();
                        if (nom.isEmpty()) {
                            nom = "Anonyme";
                        }
                        // Enregistrement du score dans la base SQLite
                        DatabaseHelper db = new DatabaseHelper(JeuActivity.this);
                        db.ajouterScore(nom, score);

                        // On affiche l'écran des meilleurs scores
                        startActivity(new Intent(JeuActivity.this, HighscoreActivity.class));
                        finish();
                    }
                })
                .show();
    }
}
