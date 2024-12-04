package com.example.routinehero;

public class Routine {
    private int id;
    private String name;
    private boolean completed;

    public Routine(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}