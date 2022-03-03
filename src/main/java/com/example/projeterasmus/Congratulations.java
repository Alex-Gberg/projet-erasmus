package com.example.projeterasmus;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Congratulations {

    private BorderPane borderPane;

    public Congratulations(Stage stage) {

        //CongratulationsMessage
        Label congratsMessage= new Label("Félicitations tu as gagné!");
        congratsMessage.setId("congratsMessage");

        //Add CongratulationsMessage to titleVBox
        VBox titleVBox = new VBox();
        titleVBox.getChildren().addAll(congratsMessage);
        titleVBox.setMaxHeight(Double.MAX_VALUE);
        titleVBox.setMaxWidth(Double.MAX_VALUE);
        titleVBox.setAlignment(Pos.BASELINE_CENTER);
        titleVBox.setSpacing(10);

        //Create two buttons: Play Again & Quit
        Button playAgainButton = new Button("Rejouer");
        playAgainButton.setId("round-green");
        playAgainButton.setOnAction(e -> {
            MainMenu.getMenuStage().setScene(MainMenu.getMenuScene());
            stage.close();
        });


        Button quitButton = new Button("Quitter le jeu");
        quitButton.setId("round-red");
        quitButton.setOnAction(e -> {
            Qui.getPrimaryStage().close();
            stage.close();
        });

        //Add both buttons to buttonBox
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(playAgainButton, quitButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        //ImageView to display GIF
        ImageView imageView = new ImageView();
        imageView.setId("imageView");
        imageView.setFitWidth(498);
        imageView.setFitHeight(224);

        Image image = new Image("formidable.gif");
        imageView.setImage(image);

        //BorderPane
        borderPane = new BorderPane();
        borderPane.setTop(titleVBox);
        borderPane.setCenter(imageView);
        borderPane.setBottom(buttonBox);
        borderPane.setId("backgroundBlack");

    }

    public BorderPane getBorderPane(){
        return borderPane;
    }

}
