package com.example.projeterasmus;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Generator {
    private final int numPics;

    public Generator(Stage stage, String imageFolder) {
        Display display = new Display(imageFolder);
        numPics = display.getNumPics();

        TextField rowInput = new TextField();
        rowInput.setPromptText("Nombre de lignes");
        TextField columnInput = new TextField();
        columnInput.setPromptText("Nombre de colonnes");

        Button validateRowColumnButton = new Button("Validation grille");
        Label infoLabel = new Label();
        validateRowColumnButton.setOnAction(e -> {
            int desiredRows = Integer.parseInt(rowInput.getText());
            int desiredCols = Integer.parseInt(columnInput.getText());
            if (desiredRows * desiredCols == numPics) {
                display.changeRowColumns(desiredRows, desiredCols);
                infoLabel.setText("Grille est de taille " + desiredRows + "x" + desiredCols);
            }
            else {
                infoLabel.setText("Entrée invalide");
            }
            rowInput.clear();
            columnInput.clear();
        });


        stage.setScene(new Scene(new VBox(new HBox(rowInput, columnInput, validateRowColumnButton, infoLabel), display.getDisplay())));
    }
}
