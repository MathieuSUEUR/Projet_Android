package com.mathieusueur.projetandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "highscores.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SCORES = "scores";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_SCORE = "score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SCORES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_SCORE + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public void insertScore(String name, int score) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_SCORE, score);
        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    public List<Score> getTopScores(int limit) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_SCORE +
                " FROM " + TABLE_SCORES +
                " ORDER BY " + COL_SCORE + " DESC" +
                " LIMIT " + limit,
                null);

        while (cursor.moveToNext()) {
            scores.add(new Score(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)));
        }
        cursor.close();
        db.close();
        return scores;
    }
}
