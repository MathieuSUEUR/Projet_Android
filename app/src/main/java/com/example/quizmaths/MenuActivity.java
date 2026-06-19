package com.example.quizmaths;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// Écran d'accueil de l'application.
// Pour l'instant il affiche simplement le menu ; la navigation
// vers les autres écrans sera ajoutée dans les étapes suivantes.
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
}
