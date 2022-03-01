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
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;

public class Game {
    private Display display;
    private int numRows;
    private int numColumns;
    private int target;
    private JsonObject root;
    private HBox guesser;
    private HBox optionsMenuBar;
    private ArrayList<Boolean> crossedOut;
    private HashMap<String, Set<String>> propertyMap;
    private ComboBox<String> propertySelector;
    private ComboBox<String> valueSelector;
    private Button guessButton;
    private Button optionButton;
    private Button testWinningButton;

    public Game(String jsonName) {
        display = new Display(jsonName);
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

        findProperties();
        guesser = new HBox();
        propertySelector = new ComboBox<>(FXCollections.observableArrayList(propertyMap.keySet()));
        valueSelector = new ComboBox<>();
        propertySelector.setOnAction(e -> valueSelector.setItems(FXCollections.observableArrayList(propertyMap.get(propertySelector.getSelectionModel().getSelectedItem()))));
        guessButton = new Button("Guess!");
        guessButton.setOnAction(e -> {
            processGuess(propertySelector.getSelectionModel().getSelectedItem(), valueSelector.getSelectionModel().getSelectedItem());
//            propertySelector.getSelectionModel().clearSelection();
//            valueSelector.getSelectionModel().clearSelection();
        });
        constructGeusser();

        optionsMenuBar = new HBox();
        optionButton = new Button("Options");
        optionButton.setOnAction(e -> openOptionsMenu());

        //For testing purposes of Congratulations message #Issue6. This button can be removed later.
        testWinningButton = new Button("Test Winning");
        testWinningButton.setOnAction(e -> openCongratulationsScene());

        constructOptionsMenu();
    }


    private void findProperties() {
        Set<String> propSet = new HashSet<>(root.getAsJsonObject("personnages").getAsJsonObject("0").keySet());
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

    public void openCongratulationsScene(){
        Stage congratulationStage = new Stage();
        Congratulations congratulations = new Congratulations(congratulationStage);
        Scene scene = new Scene(congratulations.getBorderPane(), 498, 350);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());
        congratulationStage.setScene(scene);
        congratulationStage.setTitle("Congratulations");
        congratulationStage.show();
    }

    private void openOptionsMenu(){
        Stage optionsMenuStage = new Stage();
        OptionsMenu optionsMenu = new OptionsMenu(optionsMenuStage);
        Scene scene = new Scene(optionsMenu.getDisplay());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());
        optionsMenuStage.setScene(scene);
        optionsMenuStage.setTitle("Options");
        optionsMenuStage.show();
    }

    private void processGuess(String property, String value) {
        JsonObject pers = root.getAsJsonObject("personnages");

        Boolean response = value.equals(pers.getAsJsonObject(String.valueOf(target)).get(property).getAsString());

        System.out.println("Guess -> " + property + ": " + value + "\nResponse -> " + response);

        for (int i = 0; i < numRows*numColumns; i++) {
            System.out.println(i);
            if (!response && value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                display.crossOutPic(pers.getAsJsonObject(String.valueOf(i)).get("fichier").getAsString());
            }
            else if (response && !value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                display.crossOutPic(pers.getAsJsonObject(String.valueOf(i)).get("fichier").getAsString());
            }
        }
    }

    private void constructGeusser() {
        guesser.getChildren().addAll(propertySelector, valueSelector, guessButton);
    }

    private void constructOptionsMenu() {
        optionsMenuBar.getChildren().addAll(optionButton, testWinningButton);
    }

    public Scene getGameScene() {
        Scene scene = new Scene(new VBox(optionsMenuBar, display.getDisplay(), guesser));
        return scene;
    }
}
