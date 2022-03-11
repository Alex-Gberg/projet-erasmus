package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Menu {
    public Menu(Stage stage) {
        // Create a dropdown menu to select which character set to use
        ArrayList<String> playableGames = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("src/main/resources/JSON").list())));
        for (int i = 0; i < playableGames.size(); i++) {
            playableGames.set(i, playableGames.get(i).substring(0, playableGames.get(i).length()-5));
        }
        ComboBox<String> characterSetSelector = new ComboBox<>(FXCollections.observableArrayList(playableGames));
        characterSetSelector.setPromptText("Choisir le type de caractères");

        // Create 4 Buttons for new game, load game, generate game, quit game
        Button newGameButton = new Button("Nouveau jeu");
        newGameButton.setId("round-green");
        newGameButton.setOnAction(e -> {
            String selected = characterSetSelector.getSelectionModel().getSelectedItem();
            if (selected != null) {
                new Game(stage, selected + ".json");
            }
        });

        Button loadGameButton = new Button("Continuer la partie");
        loadGameButton.setId("round-yellow");
        loadGameButton.setOnAction(e -> {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader("src/main/resources/save.json"));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            JsonObject root = json.getAsJsonObject();

            ArrayList<Boolean> crossedOut = new ArrayList<>();
            for (JsonElement o : root.get("crossedOut").getAsJsonArray()) {
                crossedOut.add(o.getAsBoolean());
            }

            new Game(stage, root.get("generator").getAsString(), root.get("target").getAsInt(), crossedOut);
        });

        // Create a dropdown menu to select which image set to use
        ComboBox<String> imageSetSelector = new ComboBox<>(FXCollections.observableArrayList(new File("src/main/resources/character_sets").list()));
        imageSetSelector.setPromptText("Choisir les images");

        Button generateButton = new Button("Générer un jeu");
        generateButton.setId("round-green");
        generateButton.setOnAction(e -> {
            String selected = imageSetSelector.getSelectionModel().getSelectedItem();
            if (selected != null) {
                new Generator(stage, selected);
            }
        });

        Button quitGameButton = new Button("Quitter le jeu");
        quitGameButton.setId("round-red");
        quitGameButton.setOnAction(e -> stage.close());

        // Create Borderpane Layout
        BorderPane borderPane = new BorderPane();
        borderPane.setId("pane");

        // Put buttons VBox and Borderpane
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(350,0,133,80));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(characterSetSelector, newGameButton, loadGameButton, imageSetSelector, generateButton, quitGameButton);

        // Create Label
        Label tradeMarkLabel = new Label("@Qui-est-ce? - Groupe Erasmus");
        tradeMarkLabel.setId("tradeMarkLabel");

        // HBox for Label
        HBox bottomBox = new HBox();
        HBox leftBottom = new HBox();
        HBox centerBottom = new HBox();

        leftBottom.getChildren().add(tradeMarkLabel);
        centerBottom.getChildren().add(buttonsBox);

        leftBottom.setAlignment(Pos.BOTTOM_LEFT);
        centerBottom.setAlignment(Pos.BOTTOM_CENTER);
        centerBottom.setPadding(new Insets(-50));

        bottomBox.getChildren().addAll(leftBottom, centerBottom);

        borderPane.setBottom(bottomBox);


        Scene scene = new Scene(borderPane, 553, 520);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }
}