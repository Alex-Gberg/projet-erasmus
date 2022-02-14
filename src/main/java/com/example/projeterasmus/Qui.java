package com.example.projeterasmus;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;


public class Qui{

    private final BorderPane rootPane ;

    public Qui(){
        //Getting rootPane aka le size du page accueil
        rootPane = new BorderPane();
    }


    public Pane displayGameStage(Stage stage) {
        Display display = new Display("jeux.json");
        Guesser guesser = new Guesser("jeux.json");


        Scene scene = new Scene(new VBox(display.getDisplay(), guesser.getGuesser()));
        stage.setTitle("Guess Who? - Erasmus Project");
        stage.setScene(scene);
        stage.show();

        return rootPane ;
    }
    /*
    //Override
    public void start(Stage stage) throws IOException {
        Display display = new Display("jeux.json");
        Guesser guesser = new Guesser("jeux.json");


        Scene scene = new Scene(new VBox(display.getDisplay(), guesser.getGuesser()));
        stage.setTitle("Guess Who? - Erasmus Project");
        stage.setScene(scene);
        stage.show();


    }
    /*
    public static void main(String[] args) {
        launch();
    }

     */


}
