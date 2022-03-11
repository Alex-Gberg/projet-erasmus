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
import java.util.Objects;

import static java.lang.Math.sqrt;

 /*
   Display only displays the pictures.
   nodes is an arraylist holding ImageViews and Groups

   Display constructor takes JSON
   Get lignes and columns from JSON aka how pictures are listed later

 */

public class Display {
    private Game game;
    private JsonObject root;
    private int numPics;
    private int numRows;
    private int numColumns;
    private String imageFolder;
    private VBox display = new VBox();
    private ArrayList<Node> nodes = new ArrayList<>();

    // Create display based on a game/JSON
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

        numPics = loadPics(root);

        display.getChildren().setAll(fillRows());
    }

    // Create display based on the name of a folder of images
    public Display(String imageFolder) {
        numPics = loadPics(imageFolder);

        int i = (int) sqrt(numPics);
        while (i * (numPics/i) != numPics) {
            i--;
        }
        numRows = i;
        numColumns = numPics / numRows;

        display.getChildren().setAll(fillRows());
    }

    // Load images based on a JSON
    private int loadPics(JsonObject root) {
        JsonObject pers = root.getAsJsonObject("possibilites"); // All the stuff in personnage
        for (int i = 0; i < numRows*numColumns; i++) {
            JsonObject obj = pers.getAsJsonObject(String.valueOf(i));
            File file = new File(imageFolder + obj.get("fichier").getAsString());
            ImageView imageView = new ImageView(new Image(file.toURI().toString()));
            imageView.setOnMouseClicked((MouseEvent e) -> {
                ImageView iv = (ImageView) e.getTarget();
                crossOutPicManual(iv);
            });
            nodes.add(imageView);
        }
        return numRows*numColumns;
    }

    // Load images based on the contents of a folder
    private int loadPics(String imageFolder) {
        File folder = new File("src/main/resources/character_sets/" + imageFolder);
        int count = 0;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            nodes.add(new ImageView(new Image(file.toURI().toString())));
            count++;
        }
        return count;
    }

    // Make HBoxes based on the desired length of rows/columns
    private ArrayList<HBox> fillRows() {
        ArrayList<HBox> rows = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            ArrayList<Node> row = new ArrayList<>();
            for (int j = 0; j < numColumns; j++) {
                row.add(nodes.get(i*numColumns + j));
            }
            HBox rowBox = new HBox();
            rowBox.getChildren().addAll(row);
            rows.add(rowBox);
        }
        return rows;
    }

    // Cross out a picture based on its index
    public void crossOutPicAuto(int index, boolean updateCrossedOut) {
        String PNGName = root.getAsJsonObject("possibilites").getAsJsonObject(String.valueOf(index)).get("fichier").getAsString();
        Group crossedOut = crossOut(imageFolder + PNGName);
        crossedOut.setOnMouseClicked((MouseEvent e) -> {
            Group g = (Group) e.getSource();
            crossOutPicManual(g);
        });

        nodes.set(index, crossedOut);
        display.getChildren().setAll(fillRows());
        if (updateCrossedOut) { game.toggleCrossedOut(index); }
    }

    // Cross out a picture based on its object
    private void crossOutPicManual(Node node) {
        if (game.getAutoMode()) { return; }
        int index = nodes.indexOf(node);
        if (node instanceof ImageView) {
            String PNGName = root.getAsJsonObject("possibilites").getAsJsonObject(String.valueOf(index)).get("fichier").getAsString();
            Group crossedOut = crossOut(imageFolder + PNGName);
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
        display.getChildren().setAll(fillRows());
        game.toggleCrossedOut(index);
    }

    // Create the Group which consists of an image and an X over it
    private Group crossOut(String imagePath) {
        File file = new File(imagePath);
        ImageView bottom = new ImageView(new Image(file.toURI().toString()));
        ImageView top = new ImageView(new Image("RedCross.png"));
        top.setFitHeight(75);
        top.setFitWidth(75);

        return new Group(bottom, top);
    }

    public VBox getDisplay() {
        return display;
    }

    public int getNumPics() { return numPics; }

    public void changeRowColumns(int newRows, int newColumns) {
        numRows = newRows;
        numColumns = newColumns;
        display.getChildren().setAll(fillRows());
    }
}
