package com.example.projeterasmus;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class Generator {
    private final int numPics;
    private int numRows;
    private int numColumns;
    private final String imageFolder;
    private ObservableList<String> attributeList;
    private TreeMap<String, HashMap<String, String>> possibilites;

    public Generator(Stage stage, String imageFolder) {
        // for testing only
        Button testJson = new Button("Make JSON (test)");
        testJson.setOnAction(e -> makeJSON());

        this.imageFolder = imageFolder;

        Button optionButton = new Button("Options");
        optionButton.setOnAction(e -> new Options(stage).showOptions());

        Display display = new Display(imageFolder);
        numPics = display.getNumPics();
        numRows = display.getNumRowsCols()[0];
        numColumns = display.getNumRowsCols()[1];

        Label rowInputLabel = new Label("Nombre de Lignes:");
        TextField rowInput = new TextField();
        rowInput.setPromptText("Saisir un entier");
        Label columnInputLabel = new Label("Nombre de Colonnes:");
        TextField columnInput = new TextField();
        columnInput.setPromptText("Saisir un entier");

        Button validateRowColumnButton = new Button("Validation grille");
        Label infoLabel = new Label();
        validateRowColumnButton.setOnAction(e -> {
            try {
                int desiredRows = Integer.parseInt(rowInput.getText());
                int desiredCols = Integer.parseInt(columnInput.getText());
                if (desiredRows * desiredCols == numPics) {
                    display.changeRowColumns(desiredRows, desiredCols);
                    infoLabel.setText("Grille est de taille " + desiredRows + "x" + desiredCols);
                    numRows = desiredRows;
                    numColumns = desiredCols;
                }
                else {
                    infoLabel.setText("Entrée invalide");
                }
            } catch (NumberFormatException formatE) {
                infoLabel.setText("Entrée invalide");
            }
            rowInput.clear();
            columnInput.clear();
        });

        attributeList = FXCollections.observableArrayList();
        ListView<String> attributeListView = new ListView<>(attributeList);

        TextField attributeInput = new TextField();
        attributeInput.setPromptText("Saisir un attribut");

        Button addAttribute = new Button("Ajouter");
        addAttribute.setOnAction(e -> {
            String text = attributeInput.getText().toLowerCase();
            if (text.length() != 0 && !attributeList.contains(text)) {
                attributeList.add(text);
            }
            attributeInput.clear();
            attributeListView.getSelectionModel().clearSelection();
        });

        Button removeAttribute = new Button("Supprimer");
        removeAttribute.setOnAction(e -> {
            attributeList.remove(attributeListView.getSelectionModel().getSelectedItem());
            attributeListView.getSelectionModel().clearSelection();
        });

        Button saveAttributes = new Button("Valider");
        saveAttributes.setOnAction(e -> {
            instantiatePossibilites();
            // remove listview and replace with some kind of way of filling in attribute values (highlight which image is being filled?)
        });

        stage.setScene(new Scene(new VBox(
                                        optionButton,
                                        new HBox(
                                            new VBox(rowInputLabel, rowInput),
                                            new VBox(columnInputLabel, columnInput),
                                            new VBox(infoLabel, validateRowColumnButton)
                                        ),
                                        display.getDisplay(),
                                        new HBox(attributeInput, addAttribute, removeAttribute),
                                        attributeListView,
                                        new HBox(saveAttributes, testJson)
                                )));
    }

    private void instantiatePossibilites() {
        possibilites = new TreeMap<>(Comparator.comparingInt(Integer::parseInt)); // TreeMap sorts the map items by key value
        File folder = new File("src/main/resources/character_sets/" + imageFolder);
        int i = 0;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            HashMap<String, String> attributes = new LinkedHashMap<>(); // LinkedHashMap keeps the order that key/values were inserted
            attributes.put("fichier", file.getName());
            attributes.put("nom", "");
            for (String attribute : attributeList) {
                attributes.put(attribute, "");
            }
            possibilites.put(String.valueOf(i), attributes);
            i++;
        }

        System.out.println(possibilites);
    }

    private void makeJSON() {
        Gson gson = new Gson();

        HashMap<String, ? super Object> generatorMap = new HashMap<>();
        generatorMap.put("images", "src/main/resources/character_sets/" + imageFolder + "/");
        generatorMap.put("ligne", String.valueOf(numRows));
        generatorMap.put("colonne", String.valueOf(numColumns));
        generatorMap.put("possibilites", possibilites);

        // validateJSON() -> validate before saving

        System.out.println(gson.toJson(generatorMap));
    }
}
