package com.example.server.game;

public class Game {
    private final String name;
    private int numberOfPlayers;

    public Game(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void incrementNumberOfPlayers(){
        numberOfPlayers++;
    }
}
