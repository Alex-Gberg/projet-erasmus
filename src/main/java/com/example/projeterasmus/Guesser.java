package com.example.projeterasmus;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Guesser {
    HBox guesser;
    ComboBox<String> propertySelector;

    public Guesser() {
        guesser = new HBox();

    }

    public HBox getGuesser() {
        return guesser;
    }
}
