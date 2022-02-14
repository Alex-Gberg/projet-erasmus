package com.example.projeterasmus;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Qui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Display display = new Display("jeux.json");
        Guesser guesser = new Guesser("jeux.json");

        Scene scene = new Scene(new VBox(display.getDisplay(), guesser.getGuesser()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
