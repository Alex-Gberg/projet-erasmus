package com.example.projeterasmus;

import com.google.gson.Gson;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Game {
    private Stage stage;
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
    private boolean autoMode;
    private Label modeLabel;

    public Game(Stage stage, String jsonName) {
        auxConstructor(stage, jsonName);

        crossedOut = new ArrayList<>();
        for (int i = 0; i < numRows*numColumns; i++) { crossedOut.add(false); }

        Random rand = new Random();
        target = rand.nextInt(numRows*numColumns);

        System.out.println("The target is " + target + ": "+ root.getAsJsonObject("possibilites").getAsJsonObject(String.valueOf(target)).get("nom").getAsString());
    }

    public Game(Stage stage, String jsonName, int target, ArrayList<Boolean> crossedOut) {
        auxConstructor(stage, jsonName);

        this.crossedOut = crossedOut;

        this.target = target;

        System.out.println("The target is " + target + ": "+ root.getAsJsonObject("possibilites").getAsJsonObject(String.valueOf(target)).get("nom").getAsString());

        for (int i = 0; i < crossedOut.size(); i++) {
            if (crossedOut.get(i)) {
                display.crossOutPicAuto(i, false);
            }
        }
    }

    private void auxConstructor(Stage stage, String jsonName) {
        this.stage = stage;
        this.jsonName = jsonName;
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
            numRows = root.get("ligne").getAsInt();
            numColumns = root.get("column").getAsInt();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        display = new Display(this);

        propertyMap = findProperties(root);

        guesser = new HBox();
        propertySelector = new ComboBox<>(FXCollections.observableArrayList(propertyMap.keySet()));
        propertySelector.setPromptText("Choisir un attribut");
        valueSelector = new ComboBox<>();
        valueSelector.setPromptText("Choisir une valeur");
        propertySelector.setOnAction(e -> valueSelector.setItems(FXCollections.observableArrayList(propertyMap.get(propertySelector.getSelectionModel().getSelectedItem()))));
        guessButton = new Button("Interroger!");
        guessButton.setOnAction(e -> processGuess(propertySelector.getSelectionModel().getSelectedItem(), valueSelector.getSelectionModel().getSelectedItem()));
        guessResult = new Label();
        constructGuesser();

        optionButton = new Button("Options");
        optionButton.setOnAction(e -> new Options(stage, this));
        autoMode = true;
        modeLabel = new Label("Mode: " + (autoMode ? "Automatique" : "Manuel"));

        stage.setScene(new Scene(new VBox(new VBox(optionButton, modeLabel) , display.getDisplay(), guesser)));
    }

    private HashMap<String, Set<String>> findProperties(JsonObject root) {
        Set<String> propSet = new HashSet<>(root.getAsJsonObject("possibilites").getAsJsonObject("0").keySet());
        propSet.remove("fichier");
        HashMap<String, Set<String>> map = new HashMap<>();
        for (String s : propSet) {
            map.put(s, new HashSet<>());
        }

        JsonObject pers = root.getAsJsonObject("possibilites");
        for (int i = 0; i < numRows*numColumns; i++) {
            for (String s : propSet) {
                map.get(s).add(pers.getAsJsonObject(String.valueOf(i)).get(s).getAsString());
            }
        }
        return map;
    }

    private void processGuess(String property, String value) {
        if (property == null || value == null) {
            guessResult.setText("Entrée invalide");
            return;
        }

        JsonObject pers = root.getAsJsonObject("possibilites");

        boolean response = value.equals(pers.getAsJsonObject(String.valueOf(target)).get(property).getAsString());

        guessResult.setText("Réponse: " + (response ? "Oui!" : "Non"));

        System.out.println("Guess -> " + property + ": " + value + "\nResponse -> " + response);

        if (autoMode) {
            for (int i = 0; i < numRows * numColumns; i++) {
                if (!crossedOut.get(i)) {
                    if (!response && value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                        display.crossOutPicAuto(i, true);
                    } else if (response && !value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                        display.crossOutPicAuto(i, true);
                    }
                }
            }
        }

        if (property.equals("nom") && response) {
            new Congratulations(stage);
        }
    }

    public void toggleCrossedOut(int i) {
        crossedOut.set(i, !crossedOut.get(i));
    }

    public void toggleMode() {
        autoMode = !autoMode;
        modeLabel.setText("Mode: " + (autoMode ? "Automatique" : "Manuel"));
    }

    public void save() {
        Save save = new Save(jsonName, target, crossedOut);
        save.saveToFile();
    }

    private void constructGuesser() {
        guesser.getChildren().addAll(new VBox(new Label("Poser une question:"),
                                            new HBox(new VBox(new Label("Attribut:"), propertySelector),
                                                    new VBox(new Label("Valeur:"), valueSelector),
                                                    new VBox(guessResult, guessButton))));
    }

    public String getJsonName() { return jsonName; }

    public boolean getAutoMode() { return autoMode; }
}
