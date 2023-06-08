package com.example.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    private String serverUrl = "http://localhost:8081";

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label noGameNameLabel;

    @FXML
    private TextField gameNameTextField;

    @FXML
    private ListView<String> gameListView;

    @FXML
    public void initialize() {
        fetchAvailableGames();
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
        }
        else
            noGameNameLabel.setText("Please insert a game name before pressing on create!");
    }

    private void createGame(String gameName) {
        try {
            URL url = new URL(serverUrl + "/create_game");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("name", gameName);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(requestBodyBytes, 0, requestBodyBytes.length);
            }

            int responseCode = connection.getResponseCode();
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
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                ObservableList<String> gamesList = FXCollections.observableArrayList();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject gameObj = jsonArray.getJSONObject(i);
                    if (gameObj.get("name") instanceof JSONObject) {
                        JSONObject nameObj = gameObj.getJSONObject("name");
                        String gameName = nameObj.getString("name");
                        gamesList.add(gameName);
                    } else {
                        String gameName = gameObj.getString("name");
                        gamesList.add(gameName);
                    }
                }

                gameListView.setItems(gamesList);
            } else {
                System.out.println("Failed to fetch available games");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
