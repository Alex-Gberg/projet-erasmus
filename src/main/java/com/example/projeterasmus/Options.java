package com.example.projeterasmus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class Options {
    public Options(Stage mainStage, Game game) {
        Stage optionsMenuStage = new Stage();
        optionsMenuStage.setResizable(false);

        // Create label
        Label infoLabel = new Label();
        infoLabel.setStyle("-fx-text-fill:WHITE;");

        // Create 4 Buttons for Resume, Change mode, Save, Restart, Quit
        Button resumeButton = new Button("Reprendre");
        resumeButton.setId("round-green");
        resumeButton.setOnAction(e -> optionsMenuStage.close());

        Button modeButton = new Button("Changer de mode");
        modeButton.setId("round-green");
        modeButton.setOnAction(e -> {
            game.toggleMode();
            infoLabel.setText("Mode: " + (game.getAutoMode() ? "Automatique" : "Manuel"));
        });

        Button saveButton = new Button("Sauvegarder la partie");
        saveButton.setId("round-green");
        saveButton.setOnAction(e -> {
            game.save();
            infoLabel.setText("Enregistré avec succès!");
        });

        Button restartButton = new Button("Retour au menu");
        restartButton.setId("round-yellow");
        restartButton.setOnAction(e -> {
            new Menu(mainStage);
            optionsMenuStage.close();
        });

        Button quitButton = new Button("Quitter le jeu");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            mainStage.close();
            optionsMenuStage.close();
        });

        // Put buttons VBox and Borderpane
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(resumeButton, modeButton, saveButton, restartButton, quitButton, infoLabel);

        //BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(buttonsBox);
        borderPane.setId("backgroundBlack");

        borderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());
        optionsMenuStage.setScene(new Scene(borderPane));
        optionsMenuStage.setTitle("Options");
        optionsMenuStage.show();
    }
}
