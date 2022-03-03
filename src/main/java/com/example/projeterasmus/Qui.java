package com.example.projeterasmus;

import javafx.application.Application;
import javafx.stage.Stage;


public class Qui extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Qui-est-ce? - Groupe Erasmus");
        stage.setResizable(false);
        new MainMenu(stage);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
