package com.example.projeterasmus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptionsMenu {

    private BorderPane borderPane;

    public OptionsMenu(Stage stage){

        // Create 3 Buttons for Resume, Restart, Quit
        Button resumeButton = new Button("Resume");
        resumeButton.setId("round-green");
        resumeButton.setOnAction(e -> stage.close());

        Button restartButton = new Button("Restart");
        restartButton.setId("round-yellow");
        restartButton.setOnAction(e -> {
            MainMenu.getMenuStage().setScene(MainMenu.getMenuScene());
            stage.close();
        });

        Button quitButton = new Button("Quit");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            Qui.getPrimaryStage().close();
            stage.close();
        });

        // Put buttons VBox and Borderpane
        VBox buttonsBox = new VBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10,10,10,10));
        buttonsBox.setSpacing(10);
        buttonsBox.getChildren().addAll(resumeButton, restartButton, quitButton);

        //BorderPane
        borderPane = new BorderPane();
        borderPane.setCenter(buttonsBox);
        borderPane.setId("backgroundBlack");
    }

    public BorderPane getDisplay() {
        return borderPane;
    }



}
