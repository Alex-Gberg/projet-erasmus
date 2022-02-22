package com.example.projeterasmus;

import javafx.application.Application;
import javafx.stage.Stage;


public class Qui extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Guess Who? - Erasmus Project");
        stage.setResizable(false);
        MainMenu menu = new MainMenu(stage);
        stage.setScene(menu.getMenuScene());
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
