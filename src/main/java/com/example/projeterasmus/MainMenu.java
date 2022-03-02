package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

public class MainMenu {
    private static Scene scene;
    private static Stage menuStage;

    public MainMenu(Stage stage) {
        menuStage = stage;
       // Create 2 Buttons for new game, load game
        Button easyButton = new Button("New Game");
        easyButton.setId("round-green");
        easyButton.setOnAction(e -> {
            Game game = new Game("jeux.json");
            stage.setScene(game.getGameScene());
        });

        Button mediumButton = new Button("Load Game");
        mediumButton.setId("round-yellow");
        mediumButton.setOnAction(e -> {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader("src/main/resources/JSON/save.json"));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            Gson gson = new Gson();
            JsonElement json = gson.fromJson(bufferedReader, JsonElement.class);
            JsonObject root = json.getAsJsonObject();

            ArrayList<Boolean> crossedOut = new ArrayList<>();
            for (JsonElement o : root.get("crossedOut").getAsJsonArray()) {
                crossedOut.add(o.getAsBoolean());
            }

            Game game = new Game(root.get("generator").getAsString(), root.get("target").getAsInt(), crossedOut);
            stage.setScene(game.getGameScene());
        });

        // Create Borderpane Layout
        BorderPane borderPane = new BorderPane();
        borderPane.setId("pane");

        // Put buttons VBox and Borderpane
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(350,0,133,80));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(easyButton, mediumButton);

        // Create Label
        Label tradeMarkLabel = new Label("@Guess Who? - Erasmus Project");
        tradeMarkLabel.setId("tradeMarkLabel");

        // HBox for Label
        HBox bottomBox = new HBox();
        HBox leftBottom = new HBox();
        HBox centerBottom = new HBox();

        leftBottom.getChildren().add(tradeMarkLabel);
        centerBottom.getChildren().add(buttonsBox);

        leftBottom.setAlignment(Pos.BOTTOM_LEFT);
        centerBottom.setAlignment(Pos.BOTTOM_CENTER);
        centerBottom.setPadding(new Insets(-50));

        bottomBox.getChildren().addAll(leftBottom, centerBottom);

        borderPane.setBottom(bottomBox);


        scene = new Scene(borderPane, 553, 520);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());
    }

    public static Scene getMenuScene() {
        return scene;
    }

    public static Stage getMenuStage() {
        return menuStage;
    }


}