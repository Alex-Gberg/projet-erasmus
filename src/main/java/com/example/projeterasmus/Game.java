package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Game {
    private Display display;
    private String jsonName;
    private int numRows;
    private int numColumns;
    private int target;
    private JsonObject root;
    private HBox guesser;
    private ArrayList<Boolean> crossedOut;
    private HashMap<String, Set<String>> propertyMap;
    private ComboBox<String> propertySelector;
    private ComboBox<String> valueSelector;
    private Button guessButton;
    private Label guessResult;
    private Button optionButton;

    public Game(String jsonName) {
        this.jsonName = jsonName;
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
            numRows = root.get("ligne").getAsInt();
            numColumns = root.get("column").getAsInt();
            crossedOut = new ArrayList<>();
            for (int i = 0; i < numRows*numColumns; i++) { crossedOut.add(false); }
            Random rand = new Random();
            target = rand.nextInt(numRows*numColumns+1);
            System.out.println("The target is " + target + ": "+ root.getAsJsonObject("personnages").getAsJsonObject(String.valueOf(target)).get("prenom").getAsString());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        auxConstructor();
    }

    public Game(String jsonName, int target, ArrayList<Boolean> crossedOut) {
        this.jsonName = jsonName;
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
            numRows = root.get("ligne").getAsInt();
            numColumns = root.get("column").getAsInt();
            this.crossedOut = crossedOut;
            this.target = target;
            System.out.println("The target is " + target + ": "+ root.getAsJsonObject("personnages").getAsJsonObject(String.valueOf(target)).get("prenom").getAsString());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        auxConstructor();

        for (int i = 0; i < crossedOut.size(); i++) {
            if (crossedOut.get(i)) {
                display.crossOutPic(root.getAsJsonObject("personnages").getAsJsonObject(String.valueOf(i)).get("fichier").getAsString());
            }
        }
    }

    private void auxConstructor() {
        display = new Display(jsonName);

        propertyMap = findProperties(root);

        guesser = new HBox();
        propertySelector = new ComboBox<>(FXCollections.observableArrayList(propertyMap.keySet()));
        valueSelector = new ComboBox<>();
        propertySelector.setOnAction(e -> valueSelector.setItems(FXCollections.observableArrayList(propertyMap.get(propertySelector.getSelectionModel().getSelectedItem()))));
        guessButton = new Button("Guess!");
        guessButton.setOnAction(e -> {
            processGuess(propertySelector.getSelectionModel().getSelectedItem(), valueSelector.getSelectionModel().getSelectedItem());
        });
        guessResult = new Label();
        constructGeusser();

        optionButton = new Button("Options");
        optionButton.setOnAction(e -> openOptionsMenu());
    }

    private HashMap<String, Set<String>> findProperties(JsonObject root) {
        Set<String> propSet = new HashSet<>(root.getAsJsonObject("personnages").getAsJsonObject("0").keySet());
        propSet.remove("fichier");
        HashMap<String, Set<String>> map = new HashMap<>();
        for (String s : propSet) {
            map.put(s, new HashSet<>());
        }

        JsonObject pers = root.getAsJsonObject("personnages");
        for (int i = 0; i < numRows*numColumns; i++) {
            for (String s : propSet) {
                map.get(s).add(pers.getAsJsonObject(String.valueOf(i)).get(s).getAsString());
            }
        }
        return map;
    }

    public void openCongratulationsScene(){
        Stage congratulationStage = new Stage();
        Congratulations congratulations = new Congratulations(congratulationStage);
        Scene scene = new Scene(congratulations.getBorderPane(), 498, 350);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());
        congratulationStage.setScene(scene);
        congratulationStage.setTitle("Congratulations");
        congratulationStage.show();
    }

    private void openOptionsMenu() {
        Stage optionsMenuStage = new Stage();
        OptionsMenu optionsMenu = new OptionsMenu(optionsMenuStage, this);
        Scene scene = new Scene(optionsMenu.getDisplay());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());
        optionsMenuStage.setScene(scene);
        optionsMenuStage.setTitle("Options");
        optionsMenuStage.show();
    }

    private void processGuess(String property, String value) {
        if (property == null || value == null) {
            guessResult.setText("Entrée invalide");
            return;
        }

        JsonObject pers = root.getAsJsonObject("personnages");

        boolean response = value.equals(pers.getAsJsonObject(String.valueOf(target)).get(property).getAsString());

        guessResult.setText("Réponse: " + (response ? "Oui!" : "Non"));

        System.out.println("Guess -> " + property + ": " + value + "\nResponse -> " + response);

        for (int i = 0; i < numRows*numColumns; i++) {
            if (!response && value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                display.crossOutPic(pers.getAsJsonObject(String.valueOf(i)).get("fichier").getAsString());
                crossedOut.set(i, true);
            }
            else if (response && !value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                display.crossOutPic(pers.getAsJsonObject(String.valueOf(i)).get("fichier").getAsString());
                crossedOut.set(i, true);
            }
        }

        if (property.equals("prenom") && response) {
            openCongratulationsScene();
        }
    }

    public void save() {
        Save save = new Save(jsonName, target, crossedOut);
        save.saveToFile();
    }

    private void constructGeusser() {
        guesser.getChildren().addAll(propertySelector, valueSelector, guessButton, guessResult);
    }

    public Scene getGameScene() {
        return new Scene(new VBox(optionButton, display.getDisplay(), guesser));
    }
}
