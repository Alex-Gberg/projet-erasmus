package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Game {
    private Display display;
    private int numRows;
    private int numColumns;
    private int target;
    private JsonObject root;
    private HBox guesser;
    private HashMap<String, Set<String>> propertyMap;
    private ComboBox<String> propertySelector;
    private ComboBox<String> valueSelector;
    private Button guessButton;

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

    public Scene getGameScene() {
        Scene scene = new Scene(new VBox(display.getDisplay(), guesser));
        return scene;
    }
}
