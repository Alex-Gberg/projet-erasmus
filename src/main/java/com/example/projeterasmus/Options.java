package com.example.projeterasmus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class Options {
    private final Button optionsButton;
    private final Stage mainStage;
    private final Stage optionsMenuStage;
    private final VBox buttonsBox;
    private Button resumeButton;
    private Button toMenuButton;
    private Button quitButton;

    public Options(Stage mainStage) {
        this.mainStage = mainStage;

        optionsMenuStage = new Stage();
        optionsMenuStage.setResizable(false);
        optionsMenuStage.setTitle("Options");
        optionsMenuStage.initOwner(mainStage);
        optionsMenuStage.initModality(Modality.WINDOW_MODAL);

        buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);
        buttonsBox.setId("backgroundBlack");
        buttonsBox.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());

        optionsMenuStage.setScene(new Scene(buttonsBox));

        optionsButton = new Button("Options");
        optionsButton.setCancelButton(true);
        optionsButton.setOnAction(e -> showOptions());

        setDefaultOptions();
    }

    public Options(Stage mainStage, Game game) {
        this(mainStage);
        setGameOptions(game);
    }

    private void setDefaultOptions() {
        resumeButton = new Button("Reprendre");
        resumeButton.setCancelButton(true);
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

    private void setGameOptions(Game game) {
        Label infoLabel = new Label();
        infoLabel.setStyle("-fx-text-fill:WHITE;");

        Button modeButton = new Button("Changer de mode");
        modeButton.setId("round-purple");
        modeButton.setOnAction(e -> {
            game.toggleMode();
            infoLabel.setText("Mode: " + (game.getAutoMode() ? "Automatique" : "Manuel"));
        });

        Button saveButton = new Button("Sauvegarder la partie");
        saveButton.setId("round-blue");
        saveButton.setOnAction(e -> {
            game.save();
            infoLabel.setText("Enregistré avec succès!");
        });

        buttonsBox.getChildren().setAll(resumeButton, modeButton, saveButton, toMenuButton, quitButton, infoLabel);
    }

    private void showOptions() {
        optionsMenuStage.show();
    }

    public Button getOptionsButton() {
        return optionsButton;
    }
}
