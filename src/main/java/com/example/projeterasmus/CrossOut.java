package com.example.projeterasmus;

import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CrossOut {

    private HBox layout;

    public CrossOut(String PNGName) {
        Image person = new Image(PNGName);
        Image redCross = new Image("RedCross.png");

        ImageView bottom = new ImageView(person);
        ImageView top = new ImageView(redCross);
        top.setBlendMode(BlendMode.DARKEN);

        Group blend = new Group(
                bottom,
                top
        );

        layout = new HBox();
        layout.getChildren().add(blend);
    }

    public HBox getLayout() {
        return layout;
    }
}