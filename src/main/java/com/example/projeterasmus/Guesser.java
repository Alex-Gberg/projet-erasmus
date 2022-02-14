package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Guesser {
    JsonObject root;
    HBox guesser;
    ObservableList<String> properties;
    ComboBox<String> propertySelector;
    Button guessButton;

    public Guesser(String jsonName) {
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        findProperties();
        guesser = new HBox();
        propertySelector = new ComboBox<>(properties);
        guessButton = new Button("Guess!");
        constructGeusser();
    }

    private void findProperties() {
        Set<String> propSet = root.getAsJsonObject("personnages").getAsJsonObject("0").keySet();
        propSet.remove("fichier");

        properties = FXCollections.observableArrayList(propSet);
    }

    private void constructGeusser() {
        guesser.getChildren().addAll(propertySelector, guessButton);
    }

    public HBox getGuesser() {
        return guesser;
    }
}
