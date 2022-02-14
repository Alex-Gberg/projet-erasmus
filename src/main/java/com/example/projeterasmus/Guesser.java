package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Guesser {
    int numRows;
    int numColumns;
    JsonObject root;
    HBox guesser;
    HashMap<String, Set<String>> propMap;
    ObservableList<String> properties;
    ComboBox<String> propertySelector;
    ComboBox<String> valueSelector;
    Button guessButton;

    public Guesser(String jsonName) {
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
            this.numRows = root.get("ligne").getAsInt();
            this.numColumns = root.get("column").getAsInt();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        findProperties();
        guesser = new HBox();
        propertySelector = new ComboBox<>(properties);
        valueSelector = new ComboBox<>();
        propertySelector.setOnAction(e -> valueSelector.setItems(FXCollections.observableArrayList(propMap.get(propertySelector.getSelectionModel().getSelectedItem()))));
        guessButton = new Button("Guess!");
        constructGeusser();
    }

    private void findProperties() {
        Set<String> propSet = root.getAsJsonObject("personnages").getAsJsonObject("0").keySet();
        propSet.remove("fichier");
        propMap = new HashMap<>();
        for (String s : propSet) {
            propMap.put(s, new HashSet<>());
        }
        properties = FXCollections.observableArrayList(propSet);

        JsonObject pers = root.getAsJsonObject("personnages");
        for (int i = 0; i < numRows*numColumns; i++) {
            for (String s : propSet) {
                propMap.get(s).add(pers.getAsJsonObject(String.valueOf(i)).get(s).getAsString());
            }
        }
        System.out.println(propMap);
    }

    private void constructGeusser() {
        guesser.getChildren().addAll(propertySelector, valueSelector, guessButton);
    }

    public HBox getGuesser() {
        return guesser;
    }
}
