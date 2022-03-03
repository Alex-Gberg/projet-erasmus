package com.example.projeterasmus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptionsMenu {
    private BorderPane borderPane;
    private Label savedLabel;

    public OptionsMenu(Stage stage, Game game){
        // Create label
        savedLabel = new Label();
        savedLabel.setStyle("-fx-text-fill:WHITE;");

        // Create 4 Buttons for Resume, Restart, Quit, Save
        Button resumeButton = new Button("Reprendre");
        resumeButton.setId("round-green");
        resumeButton.setOnAction(e -> stage.close());

        Button restartButton = new Button("Retour au menu principal");
        restartButton.setId("round-yellow");
        restartButton.setOnAction(e -> {
            MainMenu.getMenuStage().setScene(MainMenu.getMenuScene());
            stage.close();
        });

        Button quitButton = new Button("Quitter le jeu");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            Qui.getPrimaryStage().close();
            stage.close();
        });

        Button saveButton = new Button("Sauvegarder la partie");
        saveButton.setId("round-green");
        saveButton.setOnAction(e -> {
            game.save();
            savedLabel.setText("Enregistré avec succès!");
        });

        // Put buttons VBox and Borderpane
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(resumeButton, restartButton, quitButton, saveButton, savedLabel);

        //BorderPane
        borderPane = new BorderPane();
        borderPane.setCenter(buttonsBox);
        borderPane.setId("backgroundBlack");
    }

    public BorderPane getDisplay() {
        return borderPane;
    }



}
