package com.example.projeterasmus;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class CrossOut {

    private Group blend;
    private Image personImg;

    public CrossOut(String imagePath) {
        File file = new File(imagePath);
        personImg = new Image(file.toURI().toString());
        Image redCross = new Image("RedCross.png");

        ImageView bottom = new ImageView(personImg);
        ImageView top = new ImageView(redCross);
        top.setFitHeight(75);
        top.setFitWidth(75);

        blend = new Group(bottom, top);
    }

    public Group getLayout() {
        return blend;
    }

    public Image getPersonImg() { return personImg; }
}