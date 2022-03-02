package com.example.projeterasmus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
        try (Writer writer = new FileWriter("src/main/resources/JSON/save.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(this, writer);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
