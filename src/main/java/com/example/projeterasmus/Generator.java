package com.example.projeterasmus;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

public class Generator {
    private final Stage stage;
    private final String imageFolder;
    private int numRows;
    private int numColumns;

    private final Button optionButton;
    private final Display display;
    private final Node fileNamer;
    private final Node gridSizer;
    private final Node attributesInputter;
    private final Button proceedToAttributeValueInput; // Could use a better variable name
    private Node attributeValuesInputter;
    private int currentImageIndex;

    private static final CharSequence[] ILLEGAL_CHARACTERS = { "/", "\n", "\r", "\t", "\0", "\f", "`", "?", "*", "\\", "<", ">", "|", "\"", ":" };
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

        fileNamer = makeFileNamer();
        gridSizer = makeGridSizer();
        attributesInputter = makeAttributeGetter();

        proceedToAttributeValueInput = new Button("Valider et passer aux entrées de valeur d'attribut");

        proceedToAttributeValueInput.setOnAction(e -> {
            instantiatePossibilites();
            attributeValuesInputter = makeAttributeValuesInputter();
            setValueInputStage();
        });


        setInitialStage();
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

    // TODO dont allow adding nom or fichier attributes
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

    // Returns a "widget" for inputting the values of each attribute
    private Node makeAttributeValuesInputter() {
        Label imageIndicator = new Label("Entrer les valeurs pour l'image numéro " + (currentImageIndex + 1));
        VBox singleImageDisplayVbox = new VBox();
        singleImageDisplayVbox.getChildren().add(display.getSingleImage(currentImageIndex));
        HashMap<String, TextField> textFieldMap = new HashMap<>();

        textFieldMap.put("nom", new TextField());

        for (String attribute : attributeList) {
            textFieldMap.put(attribute, new TextField());
        }

        Button nextImageButton = new Button("Valider");
        Label infoLabel = new Label();
        nextImageButton.setOnAction(e -> {
            int res = processAttributeValueInput(textFieldMap);
            if (res < 0) {
                infoLabel.setText("Remplir tous les champs!");
            }
            else {
                textFieldMap.get("nom").clear();
                for (String attribute : attributeList) {
                    textFieldMap.get(attribute).clear();
                }
                imageIndicator.setText("Entrer les valeurs pour l'image numéro " + (++currentImageIndex + 1));
                if (currentImageIndex >= (numRows * numColumns)) {
                    setEndStage();
                } else {
                    singleImageDisplayVbox.getChildren().clear();
                    singleImageDisplayVbox.getChildren().add(display.getSingleImage(currentImageIndex));
                }
                infoLabel.setText("");
            }
        });

        VBox form = new VBox(singleImageDisplayVbox);
        //VBox form = new VBox(imageIndicator);
        form.getChildren().add(imageIndicator);
        form.getChildren().add(new HBox(new Label("nom" + ": "), textFieldMap.get("nom")));
        for (String attribute : attributeList) {
            form.getChildren().add(new HBox(new Label(attribute + ": "), textFieldMap.get(attribute)));
        }
        form.getChildren().add(new HBox(nextImageButton, infoLabel));

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
        Label infoLabel = new Label();
        saveButton.setOnAction(e -> {
            String proposedName = fileNameInput.getText().strip();
            for (CharSequence c : ILLEGAL_CHARACTERS) { // TODO check for an existing file of the same name to warn of overwriting?
                if (proposedName.contains(c)) {
                    infoLabel.setText(proposedName + " n'est pas un nom de fichier valide!\n Il contient: " + c);
                    stage.sizeToScene();
                    return;
                }
            }
            saveJSON(makeGeneratorMap(), proposedName);
            fileNameInput.setEditable(false);
            saveButton.setDisable(true);
            String message = "Enregistré avec succès sous: " + proposedName;
            infoLabel.setText(message);
            GeneratorCompletion generatorCompletion = new GeneratorCompletion(stage, message, proposedName);
            generatorCompletion.showGeneratorCompletionStage();
        });

        return new VBox(
                new Label("Nommer le fichier: "),
                new HBox(fileNameInput, saveButton),
                infoLabel
        );
    }

    private void setInitialStage() {
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
                //display.getDisplay(),
                attributeValuesInputter
        )));
    }

    private void setEndStage() {
        stage.setScene(new Scene(new VBox(
                optionButton,
                display.GetJSONImageView(),
                fileNamer
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

    }

    private HashMap<String, ? super Object> makeGeneratorMap() {
        HashMap<String, ? super Object> generatorMap = new HashMap<>();
        generatorMap.put("images", "src/main/resources/character_sets/" + imageFolder + "/");
        generatorMap.put("ligne", String.valueOf(numRows));
        generatorMap.put("colonne", String.valueOf(numColumns));
        generatorMap.put("possibilites", possibilites);

        if (!validatePossibilites(generatorMap)){
            System.out.println("GeneratorMap could not be made! Return to Menu.");
            new Menu(stage);
            return null;
        }
        return generatorMap;
    }

    private Boolean validatePossibilites(HashMap<String, ? super Object> generatorMap) {
        //Check first that the Attribute Nom for each PNG is different
        for (int i = 0; i < possibilites.size() - 1; i++){
            for (int j = i + 1; j < possibilites.size(); j++) {
                if (possibilites.get(String.valueOf(i)).get("nom").equals(possibilites.get(String.valueOf(j)).get("nom"))) {
                    alert("Error: Nommage invalide! Les photos ne peuvent pas avoir le même nom! Le jeu se termine! Retour au menu!");
                    return false;
                }
            }
        }

        //Make sure that no 2 characters have exactly the same attribute set
        for (int i = 0; i < possibilites.size() - 1; i++){
            for (int j = i + 1; j < possibilites.size(); j++){
                HashMap<String, String> iAttributes = new HashMap<>(possibilites.get(Integer.toString(i)));
                HashMap<String, String> jAttributes = new HashMap<>(possibilites.get(Integer.toString(j)));
                iAttributes.remove("fichier");
                iAttributes.remove("nom");
                jAttributes.remove("fichier");
                jAttributes.remove("nom");
                if (iAttributes.equals(jAttributes)){
                    alert("Error: 2 caractères ne peuvent pas avoir exactement le même ensemble d'attributs.! Il faut savoir les distinguer! Le jeu se termine! Retour au menu!");
                    return false;
                }
            }
        }
        return true;
    }

    // Show Information Alert
    private void alert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private void saveJSON(HashMap<String, ? super Object> generatorMap, String fileName) {
        if (generatorMap == null) {
            return;
        }

        try (Writer writer = new FileWriter("src/main/resources/JSON/" + fileName + ".json")) {
            Gson gson = new Gson();
            gson.toJson(generatorMap, writer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
