package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Display {
    int numRows;
    int numColumns;
    JsonObject root;
    VBox display;
    ArrayList<ImageView> imageViews;
    ArrayList<HBox> rows;

    public Display(String jsonName) {
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
            this.numRows = root.get("ligne").getAsInt();
            this.numColumns = root.get("column").getAsInt();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        display = new VBox();
        imageViews = new ArrayList<>();
        rows = new ArrayList<>();
        loadPics();
        fillRows(numRows, numColumns);
        display.getChildren().addAll(rows);
    }

    private void loadPics() {
        String imageFolder = root.get("images").getAsString();
        JsonObject pers = root.getAsJsonObject("personnages");
        for (int i = 0; i < numRows*numColumns; i++) {
            JsonObject obj = pers.getAsJsonObject(String.valueOf(i));
            File file = new File(imageFolder + obj.get("fichier").getAsString());
            imageViews.add(new ImageView(new Image(file.toURI().toString())));
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
