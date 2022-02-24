package com.example.projeterasmus;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OptionsMenu {

    private VBox display;
    private Button menuButton;

    public OptionsMenu(){
        //Create a button
        menuButton = new Button("Options");

        display = new VBox();
        display.getChildren().add(menuButton);
    }

    public VBox getDisplay() {
        return display;
    }



}
