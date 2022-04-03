package com.example.projeterasmus;

import com.google.gson.JsonObject;

import java.util.*;

public class GameUtils {
    public static HashMap<String, Set<String>> findProperties(JsonObject jsonRoot, int numPics) {
        Set<String> propSet = new HashSet<>(jsonRoot.getAsJsonObject("possibilites").getAsJsonObject("0").keySet());
        propSet.remove("fichier");
        HashMap<String, Set<String>> map = new HashMap<>();
        for (String s : propSet) {
            map.put(s, new HashSet<>());
        }

        JsonObject pers = jsonRoot.getAsJsonObject("possibilites");
        for (int i = 0; i < numPics; i++) {
            for (String s : propSet) {
                map.get(s).add(pers.getAsJsonObject(String.valueOf(i)).get(s).getAsString());
            }
        }
        return map;
    }

    public static HashMap<ArrayList<String>, Integer> getAttributeFrequency(JsonObject jsonRoot, ArrayList<Boolean> crossedOut) {
        JsonObject pers = jsonRoot.getAsJsonObject("possibilites");
        HashMap<ArrayList<String>, Integer> map = new HashMap<>();
        for (int i = 0; i < crossedOut.size(); i++) {
            if (!crossedOut.get(i)) {
                for (String attribute : GameUtils.findProperties(jsonRoot, crossedOut.size()).keySet()) {
                    if (!attribute.equals("nom")) {
                        String value = pers.getAsJsonObject(String.valueOf(i)).get(attribute).getAsString();
                        ArrayList<String> key = new ArrayList<>(Arrays.asList(attribute, value));
                        if (map.containsKey(key)) {
                            map.replace(key, map.get(key) + 1);
                        } else {
                            map.put(new ArrayList<>(Arrays.asList(attribute, value)), 1);
                        }
                    }
                }
            }
        }
        return map;
    }

    public static ArrayList<String> algoChoice(HashMap<ArrayList<String>, Integer> attributeFrequency, ArrayList<Boolean> crossedOut, JsonObject jsonRoot) {
        int remaining = crossedOut.size() - numTrue(crossedOut);

        if (remaining == 1) {
            return new ArrayList<>(Arrays.asList("nom", jsonRoot.get("possibilites").getAsJsonObject().get(String.valueOf(findFirstFalseIndex(crossedOut))).getAsJsonObject().get("nom").getAsString()));
        }

        double half = remaining / 2.0;
        double bestDistance = Double.MAX_VALUE;
        ArrayList<String> result = new ArrayList<>();
        for (ArrayList<String> key : attributeFrequency.keySet()){
            if (Math.abs(attributeFrequency.get(key) - half) < bestDistance){
                bestDistance = Math.abs(attributeFrequency.get(key) - half);
                result = key;
            }
        }

        return result;
    }

    public static int numTrue(ArrayList<Boolean> boolArray) {
        int count = 0;
        for (Boolean b : boolArray) {
            if (b) {
                count++;
            }
        }
        return count;
    }

    public static int findFirstFalseIndex(ArrayList<Boolean> crossedOut) {
        for (int i = 0; i < crossedOut.size(); i++) {
            if (!crossedOut.get(i)) {
                return i;
            }
        }
        return -1;
    }
}
