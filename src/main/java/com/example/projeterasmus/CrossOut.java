package com.example.projeterasmus;

import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;

public class CrossOut {

    private HBox layout;

    public CrossOut(String imagePath) {
        File file = new File(imagePath);
        Image person = new Image(file.toURI().toString());
        Image redCross = new Image("RedCross.png");

        ImageView bottom = new ImageView(person);
        ImageView top = new ImageView(redCross);
        top.setFitHeight(75);
        top.setFitWidth(75);
        top.setBlendMode(BlendMode.DARKEN);

        Group blend = new Group(bottom, top);

        layout = new HBox();
        layout.getChildren().add(blend);
    }

    public HBox getLayout() {
        return layout;
    }
}