package com.mathieusueur.projetandroid;

public class Score {
    private final int id;
    private final String name;
    private final int points;

    public Score(int id, String name, int points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getPoints() { return points; }
}
