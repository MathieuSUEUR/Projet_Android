package com.example.quizmaths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Classe qui gère la base de données SQLite des scores.
// Chaque score est enregistré avec le mode de jeu joué (classique ou chronométré).
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NOM_BASE = "quizmaths.db";
    private static final int VERSION = 2;

    // Les deux modes de jeu possibles
    public static final String MODE_CLASSIQUE = "classique";
    public static final String MODE_CHRONO = "chrono";

    public DatabaseHelper(Context context) {
        super(context, NOM_BASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création de la table qui stocke les scores (avec le mode de jeu)
        db.execSQL("CREATE TABLE scores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT, " +
                "score INTEGER, " +
                "mode TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ancienneVersion, int nouvelleVersion) {
        // Si la version change, on recrée la table
        db.execSQL("DROP TABLE IF EXISTS scores");
        onCreate(db);
    }

    // Ajoute un nouveau score dans la base, pour un mode de jeu donné
    public void ajouterScore(String nom, int score, String mode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valeurs = new ContentValues();
        valeurs.put("nom", nom);
        valeurs.put("score", score);
        valeurs.put("mode", mode);
        db.insert("scores", null, valeurs);
        db.close();
    }

    // Récupère les 10 meilleurs scores d'un mode, du plus grand au plus petit
    public List<String> getTop10(String mode) {
        List<String> liste = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor curseur = db.rawQuery(
                "SELECT nom, score FROM scores WHERE mode = ? ORDER BY score DESC LIMIT 10",
                new String[]{mode});
        while (curseur.moveToNext()) {
            String nom = curseur.getString(0);
            int score = curseur.getInt(1);
            liste.add(nom + " : " + score);
        }
        curseur.close();
        db.close();
        return liste;
    }
}
