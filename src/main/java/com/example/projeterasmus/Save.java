package com.example.projeterasmus;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

public class Save {
    public String generator;
    public int target;
    public ArrayList<Boolean> playerCrossedOut;
    public boolean vsComputer;
    public boolean compFound;
    public ArrayList<Boolean> compCrossedOut;

    public Save(String generator, int target, ArrayList<Boolean> playerCrossedOut, boolean vsComputer, boolean compFound, ArrayList<Boolean> compCrossedOut) {
        this.generator = generator;
        this.target = target;
        this.playerCrossedOut = playerCrossedOut;
        this.vsComputer = vsComputer;
        this.compFound = compFound;
        this.compCrossedOut = compCrossedOut;
    }

    public void saveToFile() {
        try (Writer writer = new FileWriter("src/main/resources/save.json")) {
            Gson gson = new Gson();
            gson.toJson(this, writer);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
