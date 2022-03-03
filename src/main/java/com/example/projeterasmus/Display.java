package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

 /*
   Display only displays the pictures.
   nodes is an arraylist holding ImageViews and Groups

   Display constructor takes JSON
   Get lignes and columns from JSON aka how pictures are listed later

 */

public class Display {
    private Game game;
    private int numRows;
    private int numColumns;
    private JsonObject root;
    private String imageFolder;
    private VBox display;
    private ArrayList<Node> nodes;
    private ArrayList<String> imageViewString;
    private ArrayList<HBox> rows;

    public Display(Game game) {
        this.game = game;
        String path = "src/main/resources/JSON/" + game.getJsonName();
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
        nodes = new ArrayList<>();
        imageViewString = new ArrayList<>();
        rows = new ArrayList<>();
        loadPics();

        fillRows(numRows, numColumns);
        display.getChildren().setAll(rows);
    }

    private void loadPics() {
        JsonObject pers = root.getAsJsonObject("personnages"); // All the stuff in personnage
        for (int i = 0; i < numRows*numColumns; i++) {
            JsonObject obj = pers.getAsJsonObject(String.valueOf(i));
            File file = new File(imageFolder + obj.get("fichier").getAsString());
            imageViewString.add(imageFolder + obj.get("fichier").getAsString());
            ImageView imageView = new ImageView(new Image(file.toURI().toString()));
            imageView.setOnMouseClicked((MouseEvent e) -> {
                ImageView iv = (ImageView) e.getTarget();
                crossOutPicManual(iv);
            });
            nodes.add(imageView);
        }
    }

    private void fillRows(int numRows, int numColumns) {
        rows.clear();
        for (int i = 0; i < numRows; i++) {
            ArrayList<Node> row = new ArrayList<>();
            for (int j = 0; j < numColumns; j++) {
                row.add(nodes.get(i*numColumns + j));
            }
            HBox rowBox = new HBox();
            rowBox.getChildren().addAll(row);
            rows.add(rowBox);
        }
    }

    public void crossOutPicAuto(String PNGName, boolean updateCrossedOut) {
        Group crossedOut = new CrossOut(imageFolder + PNGName).getLayout();
        crossedOut.setOnMouseClicked((MouseEvent e) -> {
            Group g = (Group) e.getSource();
            crossOutPicManual(g);
        });

        int index = imageViewString.indexOf(imageFolder + PNGName);
        nodes.set(index, crossedOut);
        fillRows(numRows, numColumns);
        display.getChildren().setAll(rows);
        if (updateCrossedOut) { game.toggleCrossedOut(index); }
    }

    private void crossOutPicManual(Node node) {
        int index = nodes.indexOf(node);
        if (node instanceof ImageView) {
            Group crossedOut = new CrossOut(imageViewString.get(nodes.indexOf(node))).getLayout();
            crossedOut.setOnMouseClicked((MouseEvent e) -> {
                Group g = (Group) e.getSource();
                crossOutPicManual(g);
            });
            nodes.set(index, crossedOut);
        }
        else if (node instanceof Group) {
            Node n = ((Group) node).getChildren().get(0);
            if (n instanceof ImageView) {
                ImageView imgV = (ImageView) n;
                imgV.setOnMouseClicked((MouseEvent e) -> {
                    ImageView iv = (ImageView) e.getTarget();
                    crossOutPicManual(iv);
                });
                nodes.set(index, imgV);
            }
        }
        else {
            return;
        }
        fillRows(numRows, numColumns);
        display.getChildren().setAll(rows);
        game.toggleCrossedOut(index);
    }

    public VBox getDisplay() {
        return display;
    }
}
