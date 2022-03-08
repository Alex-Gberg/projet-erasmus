package com.example.projeterasmus;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Generator {
    public Generator(Stage stage, String imageFolder) {
        Display disp = new Display(imageFolder);
        stage.setScene(new Scene(disp.getDisplay()));
    }
}
