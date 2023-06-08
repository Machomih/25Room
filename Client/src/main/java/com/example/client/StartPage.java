package com.example.client;

import com.example.client.controllers.LoginRegisterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StartPage extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader =new FXMLLoader(Objects.requireNonNull(getClass().getResource("welcome-scene.fxml")));
        Parent root = loader.load();
        LoginRegisterController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Room 25 Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}