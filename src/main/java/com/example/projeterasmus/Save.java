package com.example.projeterasmus;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

public class Save {
    public String generator;
    public int target;
    public ArrayList<Boolean> crossedOut;

    public Save(String generator, int target, ArrayList<Boolean> crossedOut) {
        this.generator = generator;
        this.target = target;
        this.crossedOut = crossedOut;
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
