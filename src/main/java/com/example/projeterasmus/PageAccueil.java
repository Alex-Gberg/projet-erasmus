package com.example.projeterasmus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PageAccueil extends Application {
    @Override
    public void start(Stage stage) throws IOException {

       //Create 3 Buttons for Easy, Medium, Difficult
        Button easyButton = new Button("Easy");
        easyButton.setId("round-green");
        easyButton.setOnAction(e -> {
            Qui qui = new Qui();
            stage.getScene().setRoot(qui.displayGameStage(stage));
        });

        Button mediumButton = new Button("Medium");
        mediumButton.setId("round-yellow");
        mediumButton.setOnAction(e -> {
            Qui qui = new Qui();
            stage.getScene().setRoot(qui.displayGameStage(stage));
        });

        Button hardButton = new Button("Hard");
        hardButton.setId("round-red");
        hardButton.setOnAction(e -> {
            Qui qui = new Qui();
            stage.getScene().setRoot(qui.displayGameStage(stage));
        });

        //Create Borderpane Layout
        BorderPane borderPane = new BorderPane();
        borderPane.setId("pane");

        //Put buttons VBox and Borderpane

        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(350,0,133,80));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(easyButton, mediumButton, hardButton);


        //Create Label
        Label tradeMarkLabel = new Label("@Guess Who? - Erasmus Project");
        tradeMarkLabel.setId("tradeMarkLabel");

        //HBox for Label
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


        Scene scene = new Scene(borderPane, 553, 520);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("stylesheet.css")).toExternalForm());


        stage.setTitle("Guess Who? - Erasmus Project");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}