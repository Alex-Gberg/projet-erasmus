package com.example.projeterasmus;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Qui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Display display = new Display(3, 8);
        Scene scene = new Scene(display.getDisplay());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
