package com.example.projeterasmus;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptionsMenu {

    private VBox display;
    private Label label2;
    private Button button2;
    private Scene scene;


    public OptionsMenu(){

        display= new VBox();
        label2= new Label("This is the second scene");
        button2= new Button("Go to scene 1");
        display.getChildren().addAll(label2, button2);
    }

    public VBox getDisplay() {
        return display;
    }



}
