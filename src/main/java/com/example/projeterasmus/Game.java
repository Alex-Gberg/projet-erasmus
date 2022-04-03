package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Game {
    private Stage stage;
    private Display display;
    private boolean vsComputer;
    private Bot bot;
    private String jsonName;
    private int numRows;
    private int numColumns;
    private final int target;
    private JsonObject jsonRoot;
    private ArrayList<Boolean> crossedOut;
    private HashMap<String, Set<String>> propertyMap;
    private Label solvingAlgorithmLabel;
    private VBox botInfo;
    private boolean autoMode;
    private Label modeLabel;

    public Game(Stage stage, String jsonName, boolean vsComputer) {
        auxConstructor(stage, jsonName, vsComputer);

        crossedOut = new ArrayList<>();
        for (int i = 0; i < numRows*numColumns; i++) { crossedOut.add(false); }

        Random rand = new Random();
        target = rand.nextInt(numRows*numColumns);
        System.out.println("The target is " + target + ": "+ jsonRoot.getAsJsonObject("possibilites").getAsJsonObject(String.valueOf(target)).get("nom").getAsString());

        if (vsComputer) {
            this.bot = new Bot(jsonRoot, target, false, new ArrayList<>(crossedOut));
            botInfo.getChildren().setAll(bot.getBotInfo());
        }

        stage.sizeToScene();
    }

    public Game(Stage stage, String jsonName, int target, ArrayList<Boolean> playerCrossedOut, boolean vsComputer, boolean compFound, ArrayList<Boolean> compCrossedOut) {
        auxConstructor(stage, jsonName, vsComputer);

        this.crossedOut = playerCrossedOut;
        this.target = target;
        System.out.println("The target is " + target + ": "+ jsonRoot.getAsJsonObject("possibilites").getAsJsonObject(String.valueOf(target)).get("nom").getAsString());

        for (int i = 0; i < crossedOut.size(); i++) {
            if (crossedOut.get(i)) {
                display.crossOutPicAuto(i, false);
            }
        }

        if (vsComputer) {
            bot = new Bot(jsonRoot, target, compFound, compCrossedOut);
            botInfo = bot.getBotInfo();
        }

        stage.sizeToScene();
    }

    private void auxConstructor(Stage stage, String jsonName, boolean vsComputer) {
        this.stage = stage;
        this.vsComputer = vsComputer;
        this.jsonName = jsonName;
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            jsonRoot = json.getAsJsonObject();
            numRows = jsonRoot.get("ligne").getAsInt();
            numColumns = jsonRoot.get("colonne").getAsInt();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        display = new Display(this);

        Button optionButton = new Options(stage, this).getOptionsButton();
        autoMode = true;
        modeLabel = new Label("Mode: " + "Automatique");

        Button solvingAlgorithmButton = new Button("Meilleure question à poser");
        solvingAlgorithmLabel = new Label();
        solvingAlgorithmLabel.setId("meilleureQuestionPoser");
        solvingAlgorithmLabel.getStylesheets().add("stylesheet.css");
        solvingAlgorithmButton.setOnAction(e -> {
            ArrayList<String> res = GameUtils.algoChoice(GameUtils.getAttributeFrequency(jsonRoot, numRows*numColumns, crossedOut), crossedOut, jsonRoot);
            solvingAlgorithmLabel.setText("Attribut: " + res.get(0) + ", valeur: " + res.get(1));
            stage.sizeToScene();
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(new VBox(optionButton, modeLabel));
        borderPane.setRight(new VBox(solvingAlgorithmButton, solvingAlgorithmLabel));

        botInfo = new VBox();

        propertyMap = GameUtils.findProperties(jsonRoot, numRows*numColumns);

        stage.setScene(new Scene(new VBox(new VBox(borderPane, display.getDisplay(), botInfo, constructGuesser()))));
    }

    private boolean processGuess(String property, String value) {
        JsonObject pers = jsonRoot.getAsJsonObject("possibilites");

        boolean response = value.equals(pers.getAsJsonObject(String.valueOf(target)).get(property).getAsString());
        System.out.println("Guess -> " + property + ": " + value + " ==> Response -> " + response);

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

        return response;
    }

    public void toggleCrossedOut(int i) {
        crossedOut.set(i, !crossedOut.get(i));
    }

    public void toggleMode() {
        autoMode = !autoMode;
        modeLabel.setText("Mode: " + (autoMode ? "Automatique" : "Manuel"));
    }

    public void save() {
        ArrayList<Boolean> botCrossedOut = vsComputer ? bot.getCrossedOut() : new ArrayList<>();
        boolean botFound = vsComputer && bot.getFound();

        Save save = new Save(jsonName, target, crossedOut, vsComputer, botFound, botCrossedOut);
        save.saveToFile();
    }

    private HBox constructGuesser() {
        HBox guesser = new HBox();
        ComboBox<String> propertySelector = new ComboBox<>(FXCollections.observableArrayList(propertyMap.keySet()));
        propertySelector.setPromptText("Choisir un attribut");
        ComboBox<String> valueSelector = new ComboBox<>();
        valueSelector.setPromptText("Choisir une valeur");
        propertySelector.setOnAction(e -> valueSelector.setItems(FXCollections.observableArrayList(propertyMap.get(propertySelector.getSelectionModel().getSelectedItem()))));
        Label guessResult = new Label();
        Button guessButton = new Button("Interroger!");
        guessButton.setDefaultButton(true);
        guessButton.setOnAction(e -> {
            String property = propertySelector.getSelectionModel().getSelectedItem();
            String value = valueSelector.getSelectionModel().getSelectedItem();
            if (property == null || value == null) {
                guessResult.setText("Entrée invalide");
                return;
            }
            guessResult.setText("Réponse: " + (processGuess(property, value) ? "Oui!" : "Non"));
            if (vsComputer) {
                bot.playTurn();
                botInfo.getChildren().setAll(bot.getBotInfo());
            }
            solvingAlgorithmLabel.setText("");
            stage.sizeToScene();
        });

        Label poserQuestionLabel = new Label("Poser une question:");
        poserQuestionLabel.setId("smallTitle");
        poserQuestionLabel.getStylesheets().add("stylesheet.css");

        guesser.getChildren().addAll(new VBox(poserQuestionLabel ,
                                            new HBox(new VBox(new Label("Attribut:"), propertySelector),
                                                    new VBox(new Label("Valeur:"), valueSelector),
                                                    new VBox(guessResult, guessButton))));
        return guesser;
    }

    public String getJsonName() { return jsonName; }

    public boolean getAutoMode() { return autoMode; }
}
