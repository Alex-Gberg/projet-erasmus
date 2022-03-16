package com.example.projeterasmus;

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

public class Generator {
    private final int numPics;

    public Generator(Stage stage, String imageFolder) {
        Button optionButton = new Button("Options");
        optionButton.setOnAction(e -> new Options(stage).showOptions());

        Display display = new Display(imageFolder);
        numPics = display.getNumPics();

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

        ObservableList<String> attributeList = FXCollections.observableArrayList();
        ListView<String> attributeListView = new ListView<>(attributeList);

        TextField attributeInput = new TextField();
        attributeInput.setPromptText("Saisir un attribut");

        Button addAttribute = new Button("Ajouter");
        addAttribute.setOnAction(e -> {
            String text = attributeInput.getText();
            if (text.length() != 0) {
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

        stage.setScene(new Scene(new VBox(
                                    optionButton,
                                    new HBox(
                                        new VBox(rowInputLabel, rowInput),
                                        new VBox(columnInputLabel, columnInput),
                                        new VBox(infoLabel, validateRowColumnButton)
                                    ),
                                    display.getDisplay(),
                                    new HBox(attributeInput, addAttribute, removeAttribute),
                                    attributeListView
                            )));
    }
}
