package appinterface.controllers;

import appinterface.PSAApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import models.Game;
import models.GamesSet;
import parsers.gg.GGPokerokRushNCashParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class UploadController {
    public ArrayList<Game> uploadFiles(List<File> files) throws IOException {
        ArrayList<Game> allGames = new ArrayList<>();
        ArrayList<Exception> allExceptions = new ArrayList<>();
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        for (File f : files) {
            try {
                allGames.addAll(parser.parseFile(f.toString()));
            } catch (Exception ex) {
                allExceptions.add(ex);
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("You have uploaded " + (files.size() - allExceptions.size()) + " files, containing " +
                allGames.size() + " games.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (allExceptions.size() != 0) {
            alert.setContentText(alert.getContentText() + "\n" + allExceptions.size() + " files were in incorrect format or could not be read.");
        }
        alert.show();

        return allGames;
    }


}
