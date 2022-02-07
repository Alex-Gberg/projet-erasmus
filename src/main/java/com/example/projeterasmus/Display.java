package com.example.projeterasmus;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;

public class Display {
    int numRows;
    int numColumns;
    VBox display;
    ArrayList<ImageView> imageViews;
    ArrayList<HBox> rows;

    public Display(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        display = new VBox();
        imageViews = new ArrayList<>();
        rows = new ArrayList<>();
        loadPics(numRows, numColumns);
        fillRows(numRows, numColumns);
        display.getChildren().addAll(rows);
    }

    private void loadPics(int numRows, int numColumns) {
        for (int i = 1; i <= numRows*numColumns; i++) {
            File file = new File("src/main/resources/personnages/imageonline-co-split-image" + (i !=1 ? ("-" + i) : "") + ".png");
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageViews.add(imageView);
        }
    }

    private void fillRows(int numRows, int numColumns) {
        for (int i = 0; i < numRows; i++) {
            ArrayList<ImageView> row = new ArrayList<>();
            for (int j = 0; j < numColumns; j++) {
                row.add(imageViews.get(i*numColumns + j));
            }
            HBox rowBox = new HBox();
            rowBox.getChildren().addAll(row);
            rows.add(rowBox);
        }
    }

    public VBox getDisplay() {
        return display;
    }
}
