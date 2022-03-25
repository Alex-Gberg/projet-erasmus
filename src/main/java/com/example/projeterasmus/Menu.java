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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Menu {
    private ObservableList<String> imageSets = FXCollections.observableArrayList(new File("src/main/resources/character_sets").list());

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
            else {
                new Alert(Alert.AlertType.INFORMATION, "Le type de caractères doit être choisi avant de commencer un nouveau jeu").showAndWait();
            }
        });

        Button loadGameButton = new Button("Continuer la partie");
        loadGameButton.setId("round-yellow");
        loadGameButton.setOnAction(e -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/save.json"));
                Gson gson = new Gson();
                JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
                JsonObject root = json.getAsJsonObject();

                ArrayList<Boolean> crossedOut = new ArrayList<>();
                for (JsonElement o : root.get("crossedOut").getAsJsonArray()) {
                    crossedOut.add(o.getAsBoolean());
                }

                new Game(stage, root.get("generator").getAsString(), root.get("target").getAsInt(), crossedOut);

            } catch (FileNotFoundException ex) {
                new Alert(Alert.AlertType.INFORMATION, "Impossible de continuer la partie: aucun fichier de sauvegarde n'existe actuellement").showAndWait();
            }
        });

        // Create a dropdown menu to select which image set to use
        ComboBox<String> imageSetSelector = new ComboBox<>(imageSets);
        imageSetSelector.setPromptText("Choisir les images");

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
                if (!containsNonImages(srcDir)) {
                    if (containsAtLeastOneImage(srcDir)) {
                        if (!characterSetExists(srcDir)) {
                            copyDirectory(srcDir, destDir);
                            new Alert(Alert.AlertType.INFORMATION, srcDir + " a été importé avec succès").showAndWait();
                        }
                        else {
                            new Alert(Alert.AlertType.ERROR, "L'importation a échoué: \"" + srcDir.getName() + "\" existe déjà").showAndWait();
                        }
                    }
                    else {
                        new Alert(Alert.AlertType.ERROR, "L'importation a échoué: \"" + srcDir + "\" ne contient aucune image").showAndWait();
                    }
                }
                else {
                    new Alert(Alert.AlertType.ERROR, "L'importation a échoué: \"" + srcDir + "\" contient des fichiers qui ne sont pas des images").showAndWait();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

        Button quitGameButton = new Button("Quitter le jeu");
        quitGameButton.setId("round-red");
        quitGameButton.setOnAction(e -> stage.close());

        // Create Borderpane Layout
        BorderPane borderPane = new BorderPane();
        borderPane.setId("pane");

        // Button VBox Left
        VBox buttonBoxLeft = new VBox();
        buttonBoxLeft.setSpacing(10);
        buttonBoxLeft.setAlignment(Pos.CENTER);
        buttonBoxLeft.setPadding(new Insets(300,0,0,10));
        buttonBoxLeft.getChildren().addAll(characterSetSelector, newGameButton, loadGameButton);
        // Button VBox Right
        VBox buttonsBoxRight = new VBox();
        buttonsBoxRight.setSpacing(10);
        buttonsBoxRight.setAlignment(Pos.CENTER);
        buttonsBoxRight.setPadding(new Insets(300,10,0,0));
        buttonsBoxRight.getChildren().addAll(imageSetSelector, generateButton, importNewImageSetButton);
        // Button Box Middle
        VBox buttonsBoxMiddle = new VBox();
        buttonsBoxMiddle.setSpacing(10);
        buttonsBoxMiddle.setAlignment(Pos.CENTER);
        buttonsBoxMiddle.setPadding(new Insets(10,0,50,0));
        buttonsBoxMiddle.getChildren().add(quitGameButton);

        // HBox
        HBox leftBottom = new HBox();
        HBox rightBottom = new HBox();

        leftBottom.getChildren().add(buttonBoxLeft);
        rightBottom.getChildren().add(buttonsBoxRight);

        leftBottom.setAlignment(Pos.BOTTOM_LEFT);
        rightBottom.setAlignment(Pos.BOTTOM_RIGHT);

        borderPane.setBottom(buttonsBoxMiddle);
        borderPane.setLeft(leftBottom);
        borderPane.setRight(rightBottom);

        // Create Borderpane2 where we put borderpane in the bottom of borderpane2
        BorderPane borderPane2 = new BorderPane();
        borderPane2.setBottom(borderPane);
        borderPane2.setId("pane2");

        Scene scene = new Scene(borderPane2, 550, 450);
        scene.getStylesheets().add("stylesheet.css");
        stage.setScene(scene);
    }

    private boolean containsNonImages(File srcDir) throws IOException {
        return Files.walk(Paths.get(srcDir.toString()))
            .anyMatch(source -> {
                try {
                    String fileType = Files.probeContentType(source);
                    return !(fileType == null) && !fileType.contains("image");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return true;
                }
            });
    }

    private boolean containsAtLeastOneImage(File srcDir) throws IOException {
        return Files.walk(Paths.get(srcDir.toString()))
            .anyMatch(source -> {
                try {
                    String fileType = Files.probeContentType(source);
                    if (fileType != null) {
                        return fileType.contains("image");
                    }
                    return false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return false;
                }
            });
    }

    private void copyDirectory(File srcDir, File destDir) throws IOException {
        Files.walk(Paths.get(srcDir.toString()))
            .forEach(source -> {
                Path destination = Paths.get(destDir.toString(), source.toString().substring(srcDir.toString().length()));
                try {
                    Files.copy(source, destination);
                    imageSets.setAll(new File("src/main/resources/character_sets").list());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
    }

    private boolean characterSetExists(File srcDir) {
        ArrayList<String> existingSets = new ArrayList<>(List.of(Objects.requireNonNull(new File("src/main/resources/character_sets/").list())));
        return existingSets.contains(srcDir.getName());
    }
}