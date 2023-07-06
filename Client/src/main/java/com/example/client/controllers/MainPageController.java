package com.example.client.controllers;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class MainPageController {
    private final String serverUrl = "http://localhost:8081";

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label noGameNameLabel;

    @FXML
    private TextField gameNameTextField;

    @FXML
    private TableView<Game> gameTable;

    @FXML
    private TableColumn<Game,String> gameNameColumn;

    @FXML
    private TableColumn<Game,Integer> playersConnectedColumn;

    @FXML
    public void initialize() {
        fetchAvailableGames();
    }

    public class Game{
        private StringProperty gameName;
        private IntegerProperty numberOfPlayers;

        public Game(String gameName, Integer numberOfPlayersConnected){
            this.gameName.set(gameName);
            this.numberOfPlayers.set(numberOfPlayersConnected);
        }

        public String getGameName() {
            return gameName.get();
        }

        public StringProperty gameNameProperty() {
            if(gameName == null) gameName = new SimpleStringProperty(this,"gameName");
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName.set(gameName);
        }

        public String getNumberOfPlayers() {
            return (numberOfPlayers.get() + "/4");
        }

        public IntegerProperty numberOfPlayersProperty() {
            if(numberOfPlayers == null) numberOfPlayers = new SimpleIntegerProperty(0);
            return numberOfPlayers;
        }

        public void setNumberOfPlayers(Integer numberOfPlayers) {
            this.numberOfPlayers.set(numberOfPlayers);
        }
    }


    public void initData(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    @FXML
    private void handleCreateButtonAction() {
        String gameName = gameNameTextField.getText().trim();

        if (!gameName.isEmpty()) {
            noGameNameLabel.setText("");
            createGame(gameName);
        } else
            noGameNameLabel.setText("Please insert a game name before pressing on create!");
    }

    @FXML
    private void handleRefreshButtonAction(){
        fetchAvailableGames();
    }

    private void createGame(String gameName) {
        try {
            URL url = new URL(serverUrl + "/create_game");
            HttpURLConnection connectionToServer = (HttpURLConnection) url.openConnection();
            connectionToServer.setRequestMethod("POST");
            connectionToServer.setRequestProperty("Content-Type", "application/json");
            connectionToServer.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("name", gameName);

            try (OutputStream os = connectionToServer.getOutputStream()) {
                byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(requestBodyBytes, 0, requestBodyBytes.length);
            }

            int responseCode = connectionToServer.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Game created successfully");
            } else {
                System.out.println("Failed to create game");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fetchAvailableGames();
    }

    private void fetchAvailableGames() {
        try {
            URL url = new URL(serverUrl + "/games");
            HttpURLConnection connectionToServer = (HttpURLConnection) url.openConnection();
            connectionToServer.setRequestMethod("GET");

            int responseCode = connectionToServer.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                ObservableList<Game> gamesList = FXCollections.observableArrayList();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject gameObj = jsonArray.getJSONObject(i);
                    if (gameObj.get("name") instanceof JSONObject) {
                        JSONObject nameObj = gameObj.getJSONObject("name");
                        String gameName = nameObj.getString("name");
                        Integer playersConnected = nameObj.getInt("players");
                        gamesList.add(new Game(gameName,playersConnected));
                    } else {
                        String gameName = gameObj.getString("name");
                        Integer playersConnected = gameObj.getInt("players");
                        gamesList.add(new Game(gameName,playersConnected));
                    }
                }

                gameTable.setItems(gamesList);
                gameNameColumn.setCellFactory(new PropertyValueFactory("gameName"));
                playersConnectedColumn.setCellFactory(new PropertyValueFactory("numberOfPlayers"));

                gameTable.getColumns().setAll(gameNameColumn, playersConnectedColumn);

            } else {
                System.out.println("Failed to fetch available games");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
