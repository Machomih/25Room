package com.example.server.controllers;

import com.example.server.game.Game;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {
    private final List<Game> availableGames = new ArrayList<>();
    private final List<Game> startedGames = new ArrayList<>();

    @GetMapping("/games")
    public List<Game> getAvailableGames() {
        return availableGames;
    }

    @PostMapping("/create_game")
    public void addGame(@RequestBody Map<String, String> requestBody) {
        String gameName = requestBody.get("name");
        Game game = new Game(gameName);
        availableGames.add(game);
    }

    @PostMapping("/games/{gameName}/join")
    public void joinGame(@PathVariable String gameName) {
        Game game = checkIfGameAvailable(gameName);

        if (game != null) {
            game.incrementNumberOfPlayers();
            if (game.getNumberOfPlayers() == 4) {
                availableGames.remove(game);
                startedGames.add(game);
            }
        }
    }

    private Game checkIfGameAvailable(String gameName) {
        for (Game game : availableGames) {
            if (game.getName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }
}
