package com.example.projeterasmus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GeneratorCompletion {
    private final Stage GeneratorCompletionStage;
    private final BorderPane borderPane;

    public GeneratorCompletion(Stage mainStage, String jsonFileName) {
        GeneratorCompletionStage = new Stage();
        GeneratorCompletionStage.setResizable(false);
        GeneratorCompletionStage.setTitle("Générateur enregistré avec succès");

        Label message = new Label("Enregistré avec succès sous:\n\"" + jsonFileName + "\"");
        message.setId("label-white");

        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);

        borderPane = new BorderPane();
        borderPane.setCenter(buttonsBox);
        borderPane.setId("backgroundBlack");
        borderPane.getStylesheets().add("stylesheet.css");

        Button startGameButton = new Button("Commencer le jeu");
        startGameButton.setId("round-green");
        startGameButton.setOnAction(e -> {
            new Game(mainStage, jsonFileName + ".json");
            GeneratorCompletionStage.close();
        });

        Button toMenuButton = new Button("Retour au menu");
        toMenuButton.setId("round-yellow");
        toMenuButton.setOnAction(e -> {
            new Menu(mainStage);
            GeneratorCompletionStage.close();
        });

        Button quitButton = new Button("Quitter");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            mainStage.close();
            GeneratorCompletionStage.close();
        });

        buttonsBox.getChildren().setAll(message, startGameButton, toMenuButton, quitButton);
    }

    public void showGeneratorCompletionStage() {
        GeneratorCompletionStage.setScene(new Scene(borderPane));
        GeneratorCompletionStage.show();
    }
}
