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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Game {
    private Display display;
    private int numRows;
    private int numColumns;
    private int target;
    private JsonObject root;
    private HBox guesser;
    private VBox optionsMenu;
    private HashMap<String, Set<String>> propertyMap;
    private ComboBox<String> propertySelector;
    private ComboBox<String> valueSelector;
    private Button guessButton;
    private Button optionButton;



    public Game(String jsonName) {
        display = new Display(jsonName);
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
            this.numRows = root.get("ligne").getAsInt();
            this.numColumns = root.get("column").getAsInt();
            Random rand = new Random();
            target = rand.nextInt(numRows*numColumns+1);
            System.out.println("The target is " + target + ": "+ root.getAsJsonObject("personnages").getAsJsonObject(String.valueOf(target)).get("prenom").getAsString());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        findProperties();
        guesser = new HBox();
        propertySelector = new ComboBox<>(FXCollections.observableArrayList(propertyMap.keySet()));
        valueSelector = new ComboBox<>();
        propertySelector.setOnAction(e -> valueSelector.setItems(FXCollections.observableArrayList(propertyMap.get(propertySelector.getSelectionModel().getSelectedItem()))));
        guessButton = new Button("Guess!");
        constructGeusser();


        optionsMenu = new VBox();
        optionButton = new Button("Options");

        optionButton.setOnAction(e -> openOptions());
        constructOptionsMenu();
    }


    private void findProperties() {
        Set<String> propSet = root.getAsJsonObject("personnages").getAsJsonObject("0").keySet();
        propSet.remove("fichier");
        propertyMap = new HashMap<>();
        for (String s : propSet) {
            propertyMap.put(s, new HashSet<>());
        }

        JsonObject pers = root.getAsJsonObject("personnages");
        for (int i = 0; i < numRows*numColumns; i++) {
            for (String s : propSet) {
                propertyMap.get(s).add(pers.getAsJsonObject(String.valueOf(i)).get(s).getAsString());
            }
        }
        System.out.println(propertyMap);
    }

    private void constructGeusser() {
        guesser.getChildren().addAll(propertySelector, valueSelector, guessButton);
    }

    private void constructOptionsMenu() {
        optionsMenu.getChildren().add(optionButton);
    }

    public Scene getGameScene() {
        Scene scene = new Scene(new VBox(optionsMenu, display.getDisplay(), guesser));
        return scene;
    }



    private void openOptions(){

        Stage stage = new Stage();
        // Create 3 Buttons for Resume, Restart, Quit
        Button resumeButton = new Button("Resume");
        resumeButton.setId("round-green");
        resumeButton.setOnAction(e -> stage.close());


        Button restartButton = new Button("Restart");
        restartButton.setId("round-yellow");
        restartButton.setOnAction(e -> {
            Game game = new Game("jeux.json");
            Qui.getPrimaryStage().setScene(game.getGameScene());
            stage.close();
        });


        Button quitButton = new Button("Quit");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            Qui.getPrimaryStage().close();
            stage.close();
        });



        // Put buttons VBox and Borderpane
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(resumeButton, restartButton, quitButton);


        Scene scene = new Scene(buttonsBox);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());


        stage.setScene(scene);
        stage.show();

    }


}
