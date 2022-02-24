package com.example.projeterasmus;

import javafx.application.Application;
import javafx.stage.Stage;


public class Qui extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Guess Who? - Erasmus Project");
        primaryStage = stage;
        stage.setResizable(false);
        MainMenu menu = new MainMenu(stage);
        stage.setScene(menu.getMenuScene());
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) { launch(); }
}
