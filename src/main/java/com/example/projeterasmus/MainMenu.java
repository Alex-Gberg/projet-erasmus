package com.example.projeterasmus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainMenu {
    private static Scene scene;
    private static Stage menuStage;

    public MainMenu(Stage stage) {
        menuStage = stage;
       // Create 3 Buttons for Easy, Medium, Difficult
        Button easyButton = new Button("Easy");
        easyButton.setId("round-green");
        easyButton.setOnAction(e -> {
            Game game = new Game("jeux.json");
            stage.setScene(game.getGameScene());
        });

        Button mediumButton = new Button("Medium");
        mediumButton.setId("round-yellow");
        mediumButton.setOnAction(e -> {
            Game game = new Game("jeux.json");
            stage.setScene(game.getGameScene());
        });

        Button hardButton = new Button("Hard");
        hardButton.setId("round-red");
        hardButton.setOnAction(e -> {
            Game game = new Game("jeux.json");
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
        buttonsBox.getChildren().addAll(easyButton, mediumButton, hardButton);

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