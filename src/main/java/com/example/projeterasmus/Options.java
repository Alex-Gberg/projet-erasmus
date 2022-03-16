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
    Stage optionsMenuStage;
    BorderPane borderPane;
    VBox buttonsBox;
    Button resumeButton;
    Button toMenuButton;
    Button quitButton;

    public Options(Stage mainStage) {
        optionsMenuStage = new Stage();
        optionsMenuStage.setResizable(false);
        optionsMenuStage.setTitle("Options");

        buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);

        borderPane = new BorderPane();
        borderPane.setCenter(buttonsBox);
        borderPane.setId("backgroundBlack");
        borderPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());

        resumeButton = new Button("Reprendre");
        resumeButton.setId("round-green");
        resumeButton.setOnAction(e -> optionsMenuStage.close());

        toMenuButton = new Button("Retour au menu");
        toMenuButton.setId("round-yellow");
        toMenuButton.setOnAction(e -> {
            new Menu(mainStage);
            optionsMenuStage.close();
        });

        quitButton = new Button("Quitter");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            mainStage.close();
            optionsMenuStage.close();
        });

        buttonsBox.getChildren().setAll(resumeButton, toMenuButton, quitButton);
    }

    public Options(Stage mainStage, Game game) {
        this(mainStage);

        Label infoLabel = new Label();
        infoLabel.setStyle("-fx-text-fill:WHITE;");

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

        buttonsBox.getChildren().setAll(resumeButton, modeButton, saveButton, toMenuButton, quitButton, infoLabel);
    }

    public void showOptions() {
        optionsMenuStage.setScene(new Scene(borderPane));
        optionsMenuStage.show();
    }
}
