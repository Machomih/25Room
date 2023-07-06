package com.example.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginRegisterController {
    private Stage primaryStage;


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label warningLabel;

    private final String serverUrl = "http://localhost:8081";

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            warningLabel.setText("You need to insert both username and password");
        } else {
            String loginEndpoint = serverUrl + "/login";
            String loginPayload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

            try {
                sendPostRequest(loginEndpoint, loginPayload);
            } catch (IOException e) {
                warningLabel.setText("Failed to connect to the server.");
            }
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty())
            warningLabel.setText("You need to insert both username and password");
        else {
            String registerEndpoint = serverUrl + "/register";
            String registerPayload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

            try {
                sendPostRequest(registerEndpoint, registerPayload);

            } catch (IOException e) {
                warningLabel.setText("Failed to connect to the server.");
            }
        }
    }

    private void sendPostRequest(String endpoint, String payload) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection connectionToServer = (HttpURLConnection) url.openConnection();
        connectionToServer.setRequestMethod("POST");
        connectionToServer.setRequestProperty("Content-Type", "application/json");
        connectionToServer.setDoOutput(true);

        try (OutputStream outputStream = connectionToServer.getOutputStream()) {
            byte[] payloadBytes = payload.getBytes();
            outputStream.write(payloadBytes, 0, payloadBytes.length);
        }

        int responseCode = connectionToServer.getResponseCode();
        System.out.println(responseCode);
        System.out.println(connectionToServer.getResponseMessage());

        if (responseCode == HttpURLConnection.HTTP_OK) {
            warningLabel.setText("Success");
            switchToMainPage();
        } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            warningLabel.setText(connectionToServer.getResponseMessage());
        } else
            warningLabel.setText("Username or password are invalid!");

        connectionToServer.disconnect();
    }

    private void switchToMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/main-page.fxml"));
            Parent mainPageRoot = loader.load();
            MainPageController mainPageController = loader.getController();
            mainPageController.initData(usernameField.getText());
            Scene mainPageScene = new Scene(mainPageRoot);
            primaryStage.setScene(mainPageScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}