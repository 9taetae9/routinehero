package com.example.routinehero;

public class Routine {
    private int id;
    private String name;
    private boolean completed;

    // JSON 직렬화를 위한 기본 생성자
    public Routine() {
    }

    public Routine(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}