package com.example.quizmaths;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

// Écran de jeu : on pose des calculs au joueur.
// Le joueur a 3 vies (cœurs) et gagne 10 points par bonne réponse.
// La réponse est tapée avec un clavier de chiffres affiché dans l'application.
public class JeuActivity extends AppCompatActivity {

    private int vies = 3;
    private int score = 0;
    private int bonneReponse;     // résultat attendu de la question en cours
    private int pointsQuestion;   // points gagnés si la réponse est bonne (1 ou 5)
    private String saisie = "";   // chiffres tapés par le joueur

    private ImageView coeur1, coeur2, coeur3;
    private TextView texteScore;
    private TextView texteQuestion;
    private TextView texteReponse;

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);

        coeur1 = findViewById(R.id.coeur1);
        coeur2 = findViewById(R.id.coeur2);
        coeur3 = findViewById(R.id.coeur3);
        texteScore = findViewById(R.id.texte_score);
        texteQuestion = findViewById(R.id.texte_question);
        texteReponse = findViewById(R.id.texte_reponse);

        // Bouton "Quitter" : arrête la partie
        findViewById(R.id.bouton_quitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finDePartie();
            }
        });

        // Clavier numérique : on relie les boutons des chiffres 0 à 9
        int[] idsChiffres = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        for (int i = 0; i < idsChiffres.length; i++) {
            final String chiffre = String.valueOf(i);
            findViewById(idsChiffres[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ajouterChiffre(chiffre);
                }
            });
        }

        // Bouton effacer : supprime le dernier chiffre saisi
        findViewById(R.id.btn_effacer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effacer();
            }
        });

        // Bouton OK : valide la réponse
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valider();
            }
        });

        // On affiche la première question et l'état de départ
        nouvelleQuestion();
        majScore();
        majVies();
    }

    // Génère une nouvelle question avec une opération aléatoire
    private void nouvelleQuestion() {
        int operateur = random.nextInt(4); // 0=+, 1=-, 2=*, 3=/
        int a, b;
        String signe;

        switch (operateur) {
            case 0: // addition (rapporte 1 point)
                a = random.nextInt(50) + 1;
                b = random.nextInt(50) + 1;
                bonneReponse = a + b;
                signe = "+";
                pointsQuestion = 1;
                break;
            case 1: // soustraction (a >= b pour éviter les négatifs ; 1 point)
                a = random.nextInt(50) + 1;
                b = random.nextInt(a) + 1;
                bonneReponse = a - b;
                signe = "-";
                pointsQuestion = 1;
                break;
            case 2: // multiplication (rapporte 2 points)
                a = random.nextInt(10) + 1;
                b = random.nextInt(10) + 1;
                bonneReponse = a * b;
                signe = "×";
                pointsQuestion = 2;
                break;
            default: // division entière : a % b == 0 (rapporte 2 points)
                b = random.nextInt(9) + 1;            // diviseur entre 1 et 9
                bonneReponse = random.nextInt(9) + 1; // quotient entre 1 et 9
                a = b * bonneReponse;                 // ainsi a / b est entier
                signe = "÷";
                pointsQuestion = 2;
                break;
        }

        texteQuestion.setText(a + " " + signe + " " + b + " = ?");
        // On remet à zéro la réponse en cours
        saisie = "";
        texteReponse.setText("");
    }

    // Met à jour l'affichage du score
    private void majScore() {
        texteScore.setText(getString(R.string.score_actuel, score));
    }

    // Affiche un cœur par vie restante (les cœurs perdus disparaissent)
    private void majVies() {
        coeur1.setVisibility(vies >= 1 ? View.VISIBLE : View.INVISIBLE);
        coeur2.setVisibility(vies >= 2 ? View.VISIBLE : View.INVISIBLE);
        coeur3.setVisibility(vies >= 3 ? View.VISIBLE : View.INVISIBLE);
    }

    // Ajoute un chiffre à la réponse en cours
    private void ajouterChiffre(String chiffre) {
        if (saisie.length() < 6) { // on limite la longueur de la réponse
            saisie += chiffre;
            texteReponse.setText(saisie);
        }
    }

    // Supprime le dernier chiffre de la réponse
    private void effacer() {
        if (!saisie.isEmpty()) {
            saisie = saisie.substring(0, saisie.length() - 1);
            texteReponse.setText(saisie);
        }
    }

    // Vérifie la réponse saisie par le joueur
    private void valider() {
        if (saisie.isEmpty()) {
            Toast.makeText(this, R.string.entrez_reponse, Toast.LENGTH_SHORT).show();
            return;
        }

        int reponse = Integer.parseInt(saisie);
        if (reponse == bonneReponse) {
            // +1 pour addition/soustraction, +5 pour multiplication/division
            score += pointsQuestion;
            majScore();
        } else {
            vies--;
            majVies();
        }

        // Plus de vies : la partie est terminée
        if (vies <= 0) {
            finDePartie();
        } else {
            nouvelleQuestion();
        }
    }

    // Fin de partie : on enregistre le score sauf si le joueur quitte
    // sans avoir rien fait (aucun point gagné ET aucune vie perdue).
    private void finDePartie() {
        if (score == 0 && vies == 3) {
            // Rien joué : on ne met rien dans le classement, retour au menu
            finish();
            return;
        }

        // Sinon (points gagnés OU erreurs commises) on enregistre le score
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
