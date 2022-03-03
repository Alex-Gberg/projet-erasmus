package com.example.projeterasmus;

import javafx.application.Application;
import javafx.stage.Stage;


public class Qui extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) {
        stage.setTitle("Qui-est-ce? - Groupe Erasmus");
        primaryStage = stage;
        stage.setResizable(false);
        MainMenu menu = new MainMenu(stage);
        stage.setScene(MainMenu.getMenuScene());
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) { launch(); }
}
