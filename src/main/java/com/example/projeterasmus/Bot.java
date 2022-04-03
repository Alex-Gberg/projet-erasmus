package com.example.projeterasmus;

import com.google.gson.JsonObject;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class Bot {
    final JsonObject jsonRoot;
    final int target;
    ArrayList<Boolean> crossedOut;
    final int numPics;
    boolean found;

    public Bot(JsonObject jsonRoot, int target, boolean found, ArrayList<Boolean> crossedOut) {
        this.jsonRoot = jsonRoot;
        this.target = target;
        this.crossedOut = crossedOut;
        numPics = crossedOut.size();
        this.found = found;
    }

    public void playTurn() {
        if (!found) {
            ArrayList<String> qToAsk = GameUtils.algoChoice(GameUtils.getAttributeFrequency(jsonRoot, crossedOut), crossedOut, jsonRoot);
            if (qToAsk.get(0).equals("nom")) {
                System.out.println("Bot: Found -> " + qToAsk.get(1));
                found = true;
            } else {
                processGuess(qToAsk.get(0), qToAsk.get(1));
            }
        }
    }

    private void processGuess(String property, String value) {
        JsonObject pers = jsonRoot.getAsJsonObject("possibilites");

        boolean response = value.equals(pers.getAsJsonObject(String.valueOf(target)).get(property).getAsString());
        System.out.println("Bot: Guess -> " + property + ": " + value + " ==> Response -> " + response);

        for (int i = 0; i < numPics; i++) {
            if (!crossedOut.get(i)) {
                if (!response && value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                    crossedOut.set(i, true);
                } else if (response && !value.equals(pers.getAsJsonObject(String.valueOf(i)).get(property).getAsString())) {
                    crossedOut.set(i, true);
                }
            }
        }
    }

    public VBox getBotInfo() {
        int numCrossedOut = GameUtils.numTrue(crossedOut);
        int numRemaining = (numPics - numCrossedOut);
        Label title = new Label("La progression de votre adversaire (l'ordi):");
        title.setId("smallTitle");
        title.getStylesheets().add("stylesheet.css");
        Label crossedOutLabel = new Label("Nombre de caractères éliminés: " + numCrossedOut);
        Label remainingLabel  = new Label("Nombre de caractères restants: " + numRemaining);

        if (found) {
            return new VBox(title, new Label("L'ordinateur a trouvé qui c'est!"));
        }
        else {
            return new VBox(title, crossedOutLabel, remainingLabel);
        }
    }

    public ArrayList<Boolean> getCrossedOut() {
        return crossedOut;
    }

    public boolean getFound() {
        return found;
    }
}
