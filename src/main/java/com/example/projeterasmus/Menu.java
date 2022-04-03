package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Menu {
    private ObservableList<String> imageSets = FXCollections.observableArrayList(new File("src/main/resources/character_sets").list());

    public Menu(Stage stage) {
        // Dropdown menu to select which character set to play with
        ArrayList<String> playableGames = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("src/main/resources/JSON").list())));
        for (int i = 0; i < playableGames.size(); i++) {
            playableGames.set(i, playableGames.get(i).substring(0, playableGames.get(i).length()-5));
        }
        ComboBox<String> characterSetSelector = new ComboBox<>(FXCollections.observableArrayList(playableGames));
        characterSetSelector.setPromptText("Choisir le type de caractères");

        // New game button
        Button newGameButton = new Button("Nouveau jeu");
        newGameButton.setId("round-green");
        newGameButton.setOnAction(e -> {
            String selected = characterSetSelector.getSelectionModel().getSelectedItem();
            if (selected != null) {
                new Game(stage, selected + ".json", false);
            }
            else {
                new Alert(Alert.AlertType.INFORMATION, "Le type de caractères doit être choisi avant de commencer un nouveau jeu").showAndWait();
            }
        });

        // New game against computer button
        Button vsComputerButton = new Button("Jouer contre l'ordi");
        vsComputerButton.setId("round-blue");
        vsComputerButton.setOnAction(e -> {
            String selected = characterSetSelector.getSelectionModel().getSelectedItem();
            if (selected != null) {
                new Game(stage, selected + ".json", true);
            }
            else {
                new Alert(Alert.AlertType.INFORMATION, "Le type de caractères doit être choisi avant de commencer un nouveau jeu").showAndWait();
            }
        });

        // Load game button
        Button loadGameButton = new Button("Continuer la partie");
        loadGameButton.setId("round-yellow");
        loadGameButton.setOnAction(e -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/save.json"));
                Gson gson = new Gson();
                JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
                JsonObject jsonRoot = json.getAsJsonObject();

                ArrayList<Boolean> playerCrossedOut = new ArrayList<>();
                for (JsonElement o : jsonRoot.get("playerCrossedOut").getAsJsonArray()) {
                    playerCrossedOut.add(o.getAsBoolean());
                }

                ArrayList<Boolean> compCrossedOut = new ArrayList<>();
                for (JsonElement o : jsonRoot.get("compCrossedOut").getAsJsonArray()) {
                    compCrossedOut.add(o.getAsBoolean());
                }

                new Game(stage, jsonRoot.get("generator").getAsString(), jsonRoot.get("target").getAsInt(), playerCrossedOut, jsonRoot.get("vsComputer").getAsBoolean(), jsonRoot.get("compFound").getAsBoolean(), compCrossedOut);

            } catch (FileNotFoundException ex) {
                new Alert(Alert.AlertType.INFORMATION, "Impossible de continuer la partie: aucun fichier de sauvegarde n'existe actuellement").showAndWait();
            }
        });

        // Create a dropdown menu to select which image set to use for the generator
        ComboBox<String> imageSetSelector = new ComboBox<>(imageSets);
        imageSetSelector.setPromptText("Choisir les images");

        // Start generating a game button
        Button generateButton = new Button("Générer un jeu");
        generateButton.setId("round-green");
        generateButton.setOnAction(e -> {
            String selected = imageSetSelector.getSelectionModel().getSelectedItem();
            if (selected != null) {
                new Generator(stage, selected);
            }
            else {
                new Alert(Alert.AlertType.INFORMATION, "Les images doivent être choisi avant de commencer la génération d'un jeu").showAndWait();
            }
        });

        // Import image sets button
        Button importNewImageSetButton = new Button("Importer des images");
        importNewImageSetButton.setId("round-yellow");
        importNewImageSetButton.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(new File("/"));
            dc.setTitle("Sélectionner un dossier");
            File srcDir = dc.showDialog(new Stage());
            if (srcDir == null) { return; }
            File destDir = new File("src/main/resources/character_sets/" + srcDir.getName());
            try {
                if (MenuUtils.isValidDirectory(srcDir)) {
                    MenuUtils.copyDirectory(srcDir, destDir);
                    imageSets.setAll(new File("src/main/resources/character_sets").list());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Quit game button
        Button quitGameButton = new Button("Quitter le jeu");
        quitGameButton.setId("round-red");
        quitGameButton.setOnAction(e -> stage.close());

        // Button VBox Left
        VBox buttonBoxLeft = new VBox();
        buttonBoxLeft.setSpacing(10);
        buttonBoxLeft.setAlignment(Pos.CENTER);
        buttonBoxLeft.setPadding(new Insets(300,0,0,10));
        Label gameTitle = new Label("Jouer");
        gameTitle.setId("label-white");
        buttonBoxLeft.getChildren().addAll(gameTitle, characterSetSelector, newGameButton, vsComputerButton, loadGameButton);
        // Button VBox Right
        VBox buttonsBoxRight = new VBox();
        buttonsBoxRight.setSpacing(10);
        buttonsBoxRight.setAlignment(Pos.CENTER);
        buttonsBoxRight.setPadding(new Insets(300,10,0,0));
        Label generatorTitle = new Label("Générer");
        generatorTitle.setId("label-white");
        buttonsBoxRight.getChildren().addAll(generatorTitle, imageSetSelector, generateButton, importNewImageSetButton);
        // Button Box Middle
        VBox buttonsBoxMiddle = new VBox();
        buttonsBoxMiddle.setSpacing(10);
        buttonsBoxMiddle.setAlignment(Pos.CENTER);
        buttonsBoxMiddle.setPadding(new Insets(10,0,50,0));
        buttonsBoxMiddle.getChildren().addAll(quitGameButton);
        // Create Borderpane Layout
        BorderPane borderPane = new BorderPane();
        borderPane.setId("pane");
        borderPane.setBottom(buttonsBoxMiddle);
        borderPane.setLeft(buttonBoxLeft);
        borderPane.setRight(buttonsBoxRight);

        Scene scene = new Scene(borderPane, 550, 520);
        scene.getStylesheets().add("stylesheet.css");
        stage.setScene(scene);
    }
}