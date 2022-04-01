package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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
    private Stage stage;
    private Display display;
    private String jsonName;
    private int numRows;
    private int numColumns;
    private final int target;
    private JsonObject root;
    private HBox guesser;
    private ArrayList<Boolean> crossedOut;
    private HashMap<String, Set<String>> propertyMap;
    private ComboBox<String> propertySelector;
    private ComboBox<String> valueSelector;
    private Button guessButton;
    private Label guessResult;
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
            numColumns = root.get("colonne").getAsInt();
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
        Label solvingAlgorithmLabel = new Label();
        guessButton = new Button("Interroger!");
        guessButton.setDefaultButton(true);
        guessButton.setOnAction(e -> {
            processGuess(propertySelector.getSelectionModel().getSelectedItem(), valueSelector.getSelectionModel().getSelectedItem());
            solvingAlgorithmLabel.setText("");
        });
        guessResult = new Label();
        constructGuesser();

        Button solvingAlgorithmButton = new Button("Meilleure question à poser");
        solvingAlgorithmLabel.setId("meilleureQuestionPoser");
        solvingAlgorithmLabel.getStylesheets().add("stylesheet.css");
        solvingAlgorithmButton.setOnAction(e -> {
            ArrayList<String> res = algoChoice(countRemainingAttributeOccurences());
            solvingAlgorithmLabel.setText("Attribut: " + res.get(0) + ", valeur: " + res.get(1));
            stage.sizeToScene();
        });

        Button optionButton = new Options(stage, this).getOptionsButton();
        autoMode = true;
        modeLabel = new Label("Mode: " + "Automatique");

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setLeft(new VBox(optionButton, modeLabel));
        borderPane.setRight(new VBox(solvingAlgorithmButton, solvingAlgorithmLabel));

        stage.setScene(new Scene(new VBox(new VBox(borderPane , display.getDisplay(), guesser))));
        guessButton.requestFocus();
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
        Label poserQuestionLabel = new Label("Poser une question:");
        poserQuestionLabel.setId("smallTitle");
        poserQuestionLabel.getStylesheets().add("stylesheet.css");

        guesser.getChildren().addAll(new VBox(poserQuestionLabel ,
                                            new HBox(new VBox(new Label("Attribut:"), propertySelector),
                                                    new VBox(new Label("Valeur:"), valueSelector),
                                                    new VBox(guessResult, guessButton))));
    }

    public String getJsonName() { return jsonName; }

    public boolean getAutoMode() { return autoMode; }

    private HashMap<ArrayList<String>, Integer> countRemainingAttributeOccurences() {
        JsonObject pers = root.getAsJsonObject("possibilites");
        HashMap<ArrayList<String>, Integer> map = new HashMap<>();
        for (int i = 0; i < numRows * numColumns; i++) {
            if (!crossedOut.get(i)) {
                for (String attribute : propertyMap.keySet()) {
                    if (!attribute.equals("nom")) {
                        String value = pers.getAsJsonObject(String.valueOf(i)).get(attribute).getAsString();
                        ArrayList<String> key = new ArrayList<>(Arrays.asList(attribute, value));
                        if (map.containsKey(key)) {
                            map.replace(key, map.get(key) + 1);
                        } else {
                            map.put(new ArrayList<>(Arrays.asList(attribute, value)), 1);
                        }
                    }
                }
            }
        }
        return map;
    }

    private ArrayList<String> algoChoice(HashMap<ArrayList<String>, Integer> algoHashMap){
        //Check how many names are remaining
        int remaining = 0;
        int lastFalse = -1;
        for (int i = 0; i < crossedOut.size(); i++) {
            if (crossedOut.get(i).equals(false)) {
                remaining++;
                lastFalse = i;
            }
        }

        if (remaining == 1){
            return new ArrayList<String>(Arrays.asList("nom", root.get("possibilites").getAsJsonObject().get(String.valueOf(lastFalse)).getAsJsonObject().get("nom").getAsString()));
        }

        //Select question for algorithm to take
        //Integer division
        double half = remaining / 2.0;
        double bestDistance = Double.MAX_VALUE;
        ArrayList<String> result = new ArrayList<String>();
        for (ArrayList<String> key : algoHashMap.keySet()){
            if (Math.abs(algoHashMap.get(key) - half) < bestDistance){
                bestDistance = Math.abs(algoHashMap.get(key) - half);
                result = key;
            }
        }

        return result;
    }
}
