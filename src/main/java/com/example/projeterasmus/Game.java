package com.example.projeterasmus;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Game {
    Display display;
    Guesser guesser;

    public Game() {
        display = new Display("jeux.json");
        guesser = new Guesser("jeux.json");
    }

    public Scene getGameScene(Stage stage) {
        Scene scene = new Scene(new VBox(display.getDisplay(), guesser.getGuesser()));
        return scene;
    }
}
