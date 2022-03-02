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

 /*
   Display only displays the pictures.
   imageViews is an arraylist holding ImageViews

   Display constructor takes JSON
   Get lignes and columns from JSON aka how pictures are listed later

 */

public class Display {
    private int numRows;
    private int numColumns;
    private JsonObject root;
    private String imageFolder;
    private VBox display;
    private ArrayList<HBox> imageViewsHBox;
    private ArrayList<String> imageViewString;
    private ArrayList<HBox> rows;
    private CrossOut crossOut;

    public Display(String jsonName) {
        String path = "src/main/resources/JSON/" + jsonName;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            root = json.getAsJsonObject();
            numRows = root.get("ligne").getAsInt();
            numColumns = root.get("column").getAsInt();
            imageFolder = root.get("images").getAsString(); //Path to imageFolder
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        display = new VBox();
        imageViewsHBox = new ArrayList<>();
        imageViewString = new ArrayList<>();
        rows = new ArrayList<>();
        loadPics();
        loadPicsString();

        fillRows(numRows, numColumns);
        display.getChildren().setAll(rows);
    }

    private void loadPics() {
        JsonObject pers = root.getAsJsonObject("personnages"); // All the stuff in personnage
        for (int i = 0; i < numRows*numColumns; i++) {
            JsonObject obj = pers.getAsJsonObject(String.valueOf(i));
            File file = new File(imageFolder + obj.get("fichier").getAsString());
            ImageView imageView = new ImageView(new Image(file.toURI().toString()));
            HBox hBox = new HBox();
            hBox.getChildren().add(imageView);
            imageViewsHBox.add(hBox);
        }
    }

    private void loadPicsString() {
        JsonObject pers = root.getAsJsonObject("personnages"); // All the stuff in personnage
        for (int i = 0; i < numRows*numColumns; i++) {
            JsonObject obj = pers.getAsJsonObject(String.valueOf(i));
            File file = new File(imageFolder + obj.get("fichier").getAsString());
            imageViewString.add(imageFolder + obj.get("fichier").getAsString());
        }
    }

    private void fillRows(int numRows, int numColumns) {
        rows.clear();
        for (int i = 0; i < numRows; i++) {
            ArrayList<HBox> row = new ArrayList<>();
            for (int j = 0; j < numColumns; j++) {
                row.add(imageViewsHBox.get(i*numColumns + j));
            }
            HBox rowBox = new HBox();
            rowBox.getChildren().addAll(row);
            rows.add(rowBox);
        }
    }

    public void crossOutPic(String PNGName) {
        int index = imageViewString.indexOf(imageFolder + PNGName);
        crossOut = new CrossOut(imageFolder + PNGName);
        imageViewsHBox.set(index, crossOut.getLayout());
        fillRows(numRows, numColumns);
        display.getChildren().setAll(rows);
    }

    public VBox getDisplay() {
        return display;
    }
}
