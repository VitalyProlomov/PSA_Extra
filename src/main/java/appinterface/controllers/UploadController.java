package appinterface.controllers;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import pokerlibrary.models.Game;
import pokerlibrary.parsers.gg.GGPokerokHoldem9MaxParser;
import pokerlibrary.parsers.gg.GGPokerokRushNCashParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UploadController {
    public ArrayList<Game> uploadFiles(List<File> files) throws IOException {
        GGPokerokRushNCashParser ggPokerokRushNCashParser = new GGPokerokRushNCashParser();
        GGPokerokHoldem9MaxParser ggPokerokHoldem9MaxParser = new GGPokerokHoldem9MaxParser();

        ArrayList<Game> allGames = new ArrayList<>();
        ArrayList<Exception> allExceptions = new ArrayList<>();
        BufferedReader reader;
        for (File f : files) {
            try {
                reader = new BufferedReader(new FileReader(f));
                String firstLine;
                try {
                    firstLine = reader.readLine();
                } catch (IOException ex) {
                    throw new IOException("Could not read the first line of the file (might be because file is opened or corrupted)");
                }
                switch (firstLine.split(" ")[2].substring(1,3)) {
                    case "RC":
                      if (firstLine.split(" ")[3].equals("Hold'em")) {
                          allGames.addAll(ggPokerokRushNCashParser.parseFile(f.toString()));
                      } else {
                          throw new RuntimeException("Omaha is currently not supported");
                      }
                    case "HD":
                        String gameIdentification;
                        try {
                            gameIdentification = reader.readLine().split(" ")[2];
                        } catch (IOException ex) {
                            throw new IOException("Could not read the second line of the file (might be because file is opened or corrupted)");
                        }
                        if (gameIdentification.equals("9-max")) {
                            allGames.addAll(ggPokerokHoldem9MaxParser.parseFile(f.toString()));
                        }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
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
