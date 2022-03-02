package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OptionsMenu {
    private BorderPane borderPane;

    public OptionsMenu(Stage stage, Game game){
        // Create 4 Buttons for Resume, Restart, Quit, Save
        Button resumeButton = new Button("Resume");
        resumeButton.setId("round-green");
        resumeButton.setOnAction(e -> stage.close());

        Button restartButton = new Button("Quit to Main Menu");
        restartButton.setId("round-yellow");
        restartButton.setOnAction(e -> {
            MainMenu.getMenuStage().setScene(MainMenu.getMenuScene());
            stage.close();
        });

        Button quitButton = new Button("Quit to Desktop");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            Qui.getPrimaryStage().close();
            stage.close();
        });

        Button saveButton = new Button("Save Game");
        saveButton.setId("round-green");
        saveButton.setOnAction(e -> {
            game.save();
        });

        // Put buttons VBox and Borderpane
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(resumeButton, restartButton, quitButton, saveButton);

        //BorderPane
        borderPane = new BorderPane();
        borderPane.setCenter(buttonsBox);
        borderPane.setId("backgroundBlack");
    }

    public BorderPane getDisplay() {
        return borderPane;
    }



}
