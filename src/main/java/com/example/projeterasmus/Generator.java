package com.example.projeterasmus;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Generator {
    private final Stage stage;
    private final String imageFolder;
    private int numRows;
    private int numColumns;

    private  VBox mainVBox;
    private final Button optionButton;
    private final Display display;
    private final Node fileNamer;
    private Node attributeValuesInputter;
    private int currentImageIndex;

    private static final CharSequence[] ILLEGAL_CHARACTERS = { "/", "\n", "\r", "\t", "\0", "\f", "`", "?", "*", "\\", "<", ">", "|", "\"", ":" };
    private ObservableList<String> attributeList;
    private TreeMap<String, HashMap<String, String>> possibilites;
    private HashMap<String, ? super Object> generatorMap;

    public Generator(Stage stage, String imageFolder) {
        this.stage = stage;
        this.imageFolder = imageFolder;

        optionButton = new Button("Options");
        optionButton.setOnAction(e -> new Options(stage).showOptions());

        display = new Display(imageFolder);
        numRows = display.getNumRowsCols()[0];
        numColumns = display.getNumRowsCols()[1];

        fileNamer = makeFileNamer();
        Node gridSizer = makeGridSizer();
        Node attributesInputter = makeAttributeGetter();

        // Could use a better variable name
        Button proceedToAttributeValueInput = new Button("Valider et passer aux entrées de valeur d'attribut");

        proceedToAttributeValueInput.setOnAction(e -> {
            instantiatePossibilites();
            attributeValuesInputter = makeAttributeValuesInputter();
            mainVBox.getChildren().setAll(
                optionButton,
                attributeValuesInputter
            );
            stage.sizeToScene();
        });


        mainVBox = new VBox();
        stage.setScene(new Scene(mainVBox));
        mainVBox.getChildren().setAll(
            optionButton,
            display.getDisplay(),
                gridSizer,
                attributesInputter,
                proceedToAttributeValueInput
        );
        stage.sizeToScene();
    }

    // Returns a "widget" for adjusting the grid size
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
                    stage.sizeToScene();
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

    // Returns a "widget" for inputting the list of attributes
    private Node makeAttributeGetter() {
        attributeList = FXCollections.observableArrayList();
        ListView<String> attributeListView = new ListView<>(attributeList);
        attributeListView.setMinHeight(40.0);
        attributeListView.setPrefHeight(150.0);

        TextField attributeInput = new TextField();
        attributeInput.setPromptText("Saisir un attribut");

        Button addAttribute = new Button("Ajouter");
        addAttribute.setOnAction(e -> {
            String text = attributeInput.getText().strip().toLowerCase();
            if (text.length() != 0 && !attributeList.contains(text) && !text.equals("nom") && !text.equals("fichier")) {
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

    // Returns a "widget" for inputting the values of each attribute
    private Node makeAttributeValuesInputter() {
        Label imageIndicator = new Label("Entrer les valeurs pour l'image numéro " + (currentImageIndex + 1) + "/" + numRows*numColumns);
        VBox singleImageDisplayVbox = new VBox();
        singleImageDisplayVbox.getChildren().setAll(display.getSingleImage(currentImageIndex));
        HashMap<String, TextField> textFieldMap = new HashMap<>();

        textFieldMap.put("nom", new TextField());

        for (String attribute : attributeList) {
            textFieldMap.put(attribute, new TextField());
        }

        Button nextImageButton = new Button("Valider");
        nextImageButton.setOnAction(e -> {
            if (processAttributeValueInput(textFieldMap) < 0) {
                new Alert(Alert.AlertType.WARNING, "Il faut remplir tous les champs!").showAndWait();
            }
            else {
                currentImageIndex++;
                if (currentImageIndex >= (numRows * numColumns)) {
                    if (validatePossibilites()) {
                        generatorMap = makeGeneratorMap();
                        mainVBox.getChildren().setAll(
                            optionButton,
                            display.getDisplay(),
                            fileNamer
                        );
                        stage.sizeToScene();
                    }
                    else {
                        new Menu(stage);
                    }
                }
                else {
                    textFieldMap.get("nom").clear();
                    for (String attribute : attributeList) {
                        textFieldMap.get(attribute).clear();
                    }
                    singleImageDisplayVbox.getChildren().setAll(display.getSingleImage(currentImageIndex));
                    imageIndicator.setText("Entrer les valeurs pour l'image numéro " + (currentImageIndex + 1) + "/" + numRows*numColumns);
                    if (currentImageIndex + 1 >= (numRows * numColumns)) {
                        nextImageButton.setText("Finir");
                    }
                }
            }
        });

        VBox form = new VBox(singleImageDisplayVbox, imageIndicator);
        form.getChildren().add(new HBox(new Label("nom" + ": "), textFieldMap.get("nom")));
        for (String attribute : attributeList) {
            form.getChildren().add(new HBox(new Label(attribute + ": "), textFieldMap.get(attribute)));
        }
        form.getChildren().add(nextImageButton);
        return form;
    }

    // Takes the info from the makeAttributeValuesInputter, checks validity and inserts the values into the possiblites map
    private int processAttributeValueInput(HashMap<String, TextField> textFieldMap) {
        String nom = textFieldMap.get("nom").getText().strip().toLowerCase();
        if (nom.length() == 0) {
            textFieldMap.get("nom").clear();
            return -1;
        }
        possibilites.get(String.valueOf(currentImageIndex)).replace("nom", nom);
        for (String attribute : attributeList) {
            String userInput = textFieldMap.get(attribute).getText().strip().toLowerCase();
            if (userInput.length() == 0) {
                textFieldMap.get(attribute).clear();
                return -1;
            }
            possibilites.get(String.valueOf(currentImageIndex)).replace(attribute, userInput);
        }
        return 1;
    }

    // Returns a "widget" for naming the file
    private Node makeFileNamer() {
        TextField fileNameInput = new TextField(imageFolder + "_a_vous");
        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(e -> {
            String proposedName = fileNameInput.getText().strip();
            for (CharSequence c : ILLEGAL_CHARACTERS) {
                if (proposedName.contains(c)) {
                    new Alert(Alert.AlertType.ERROR, "\"" + proposedName + "\" n'est pas un nom de fichier valide car il contient: \"" + c + "\"").showAndWait();
                    return;
                }
            }
            try {
                if (saveJSON(generatorMap, proposedName)) {
                    fileNameInput.setEditable(false);
                    saveButton.setDisable(true);
                    GeneratorCompletion generatorCompletion = new GeneratorCompletion(stage, "Enregistré avec succès sous: \"" + proposedName + "\"", proposedName);
                    generatorCompletion.showGeneratorCompletionStage();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return new VBox(
                new Label("Nommer le fichier: "),
                new HBox(fileNameInput, saveButton)
        );
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

    }

    private HashMap<String, ? super Object> makeGeneratorMap() {
        HashMap<String, ? super Object> generatorMap = new HashMap<>();
        generatorMap.put("images", "src/main/resources/character_sets/" + imageFolder + "/");
        generatorMap.put("ligne", String.valueOf(numRows));
        generatorMap.put("colonne", String.valueOf(numColumns));
        generatorMap.put("possibilites", possibilites);

        return generatorMap;
    }

    private Boolean validatePossibilites() {
        // Check first that the Attribute Nom for each PNG is different
        for (int i = 0; i < possibilites.size() - 1; i++){
            for (int j = i + 1; j < possibilites.size(); j++) {
                if (possibilites.get(String.valueOf(i)).get("nom").equals(possibilites.get(String.valueOf(j)).get("nom"))) {
                    new Alert(Alert.AlertType.ERROR, "La génération a échoué: Nommage invalide! Les photos ne peuvent pas avoir le même nom!").showAndWait();
                    return false;
                }
            }
        }

        // Make sure that no 2 characters have exactly the same attribute set
        for (int i = 0; i < possibilites.size() - 1; i++){
            for (int j = i + 1; j < possibilites.size(); j++){
                HashMap<String, String> iAttributes = new HashMap<>(possibilites.get(Integer.toString(i)));
                HashMap<String, String> jAttributes = new HashMap<>(possibilites.get(Integer.toString(j)));
                iAttributes.remove("fichier");
                iAttributes.remove("nom");
                jAttributes.remove("fichier");
                jAttributes.remove("nom");
                if (iAttributes.equals(jAttributes)){
                    new Alert(Alert.AlertType.ERROR, "La génération a échoué: Deux caractères ne peuvent pas avoir exactement le même ensemble d'attributs!").showAndWait();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean saveJSON(HashMap<String, ? super Object> generatorMap, String fileName) throws IOException {
        if (generatorMap == null) {
            return false;
        }

        Gson gson = new Gson();
        File file = new File("src/main/resources/JSON/" + fileName + ".json");
        if (file.exists()) {
            Optional<ButtonType> confirmResult = new Alert(Alert.AlertType.CONFIRMATION, fileName + " existe déjà, voulez-vous l'écraser?").showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                try (Writer writer = new FileWriter(file)) {
                    gson.toJson(generatorMap, writer);
                }
                return true;
            }
            else {
                return false;
            }
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(generatorMap, writer);
        }
        return true;
    }
}
