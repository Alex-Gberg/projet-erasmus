package com.example.projeterasmus;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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
    private final Stage stage;
    private final String imageFolder;
    private int numRows;
    private int numColumns;

    private final Button optionButton;
    private final Display display;
    private final Node gridSizer;
    private final Node attributesInputter;
    private final Button proceedToAttributeValueInput; // Could use a better variable name
    private Node attributeValuesInputter;

    private ObservableList<String> attributeList;
    private TreeMap<String, HashMap<String, String>> possibilites;

    public Generator(Stage stage, String imageFolder) {
        this.stage = stage;
        this.imageFolder = imageFolder;

        optionButton = new Button("Options");
        optionButton.setOnAction(e -> new Options(stage).showOptions());

        display = new Display(imageFolder);
        numRows = display.getNumRowsCols()[0];
        numColumns = display.getNumRowsCols()[1];

        gridSizer = makeGridSizer();
        attributesInputter = makeAttributeGetter();

        proceedToAttributeValueInput = new Button("Valider et passer aux entrées de valeur d'attribut");

        proceedToAttributeValueInput.setOnAction(e -> {
            instantiatePossibilites();
            attributeValuesInputter = makeAttributeValuesInputter();
            setValueInputStage();
            // remove listview and replace with some kind of way of filling in attribute values (highlight which image is being filled?)
        });

        setInitialSage();
    }

    // Returns a "widget" which allows the user to adjust the grid size
    private Node makeGridSizer() {
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
                if (desiredRows * desiredCols == numRows * numColumns) {
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

        return new VBox (
                new Label("Ajuster la taille de la grille:"),
                new HBox (
                        new VBox(rowInputLabel, rowInput),
                        new VBox(columnInputLabel, columnInput),
                        new VBox(infoLabel, validateRowColumnButton)
                )
        );
    }

    private Node makeAttributeGetter() {
        attributeList = FXCollections.observableArrayList();
        ListView<String> attributeListView = new ListView<>(attributeList);
        attributeListView.setMinHeight(40.0);
        attributeListView.setPrefHeight(150.0);

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

        return new VBox (
                new Label("Définir les attributs:"),
                new HBox(attributeInput, addAttribute, removeAttribute),
                attributeListView
        );
    }

    private Node makeAttributeValuesInputter() {
        ArrayList<HBox> formItems = new ArrayList<>();
        formItems.add(new HBox(new Label("nom: "), new TextField()));
        for (String attribute : attributeList) {
            formItems.add(new HBox(new Label(attribute + ": "), new TextField()));
        }
        final int imageIndex = 0;
        Label imageIndicator = new Label("Saisir les valeurs pour image #" + imageIndex);
        VBox form = new VBox(imageIndicator);
        form.getChildren().addAll(formItems);
        Button nextImage = new Button("Valider");
        nextImage.setOnAction(e -> {
            // TODO process input data
        });
        form.getChildren().add(nextImage);

        return form;
    }

    private void setInitialSage() {
        stage.setScene(new Scene(new VBox(
                optionButton,
                display.getDisplay(),
                gridSizer,
                attributesInputter,
                proceedToAttributeValueInput
        )));
    }

    private void setValueInputStage() {
        stage.setScene(new Scene(new VBox(
                optionButton,
                display.getDisplay(),
                attributeValuesInputter
        )));
    }

    // possibilites is the Map which contains the information of each character
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
