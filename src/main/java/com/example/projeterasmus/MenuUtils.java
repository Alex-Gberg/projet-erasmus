package com.example.projeterasmus;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuUtils {
    public static boolean containsNonImages(File srcDir) throws IOException {
        return Files.walk(Paths.get(srcDir.toString()))
                .anyMatch(source -> {
                    try {
                        String fileType = Files.probeContentType(source);
                        return !(fileType == null) && !fileType.contains("image");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        return true;
                    }
                });
    }

    public static boolean containsAtLeastOneImage(File srcDir) throws IOException {
        return Files.walk(Paths.get(srcDir.toString()))
                .anyMatch(source -> {
                    try {
                        String fileType = Files.probeContentType(source);
                        if (fileType != null) {
                            return fileType.contains("image");
                        }
                        return false;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                });
    }

    public static boolean characterSetExists(File srcDir) {
        ArrayList<String> existingSets = new ArrayList<>(List.of(Objects.requireNonNull(new File("src/main/resources/character_sets/").list())));
        return existingSets.contains(srcDir.getName());
    }

    public static boolean isValidDirectory(File srcDir) throws IOException{
        if (!containsNonImages(srcDir)) {
            if (containsAtLeastOneImage(srcDir)) {
                if (!characterSetExists(srcDir)) {
                    return true;
                }
                else {
                    new Alert(Alert.AlertType.ERROR, "L'importation a échoué: \"" + srcDir.getName() + "\" existe déjà").showAndWait();
                    return false;
                }
            }
            else {
                new Alert(Alert.AlertType.ERROR, "L'importation a échoué: \"" + srcDir + "\" ne contient aucune image").showAndWait();
                return false;
            }
        }
        else {
            new Alert(Alert.AlertType.ERROR, "L'importation a échoué: \"" + srcDir + "\" contient des fichiers qui ne sont pas des images").showAndWait();
            return false;
        }
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        Files.walk(Paths.get(srcDir.toString()))
                .forEach(source -> {
                    Path destination = Paths.get(destDir.toString(), source.toString().substring(srcDir.toString().length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
        new Alert(Alert.AlertType.INFORMATION, srcDir + " a été importé avec succès").showAndWait();
    }
}
