package parsers.gg;

import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Game;
import parsers.Parser;

import java.io.*;
import java.util.ArrayList;

/**
 * Interface for parsers of games that take place in GG poker room
 */
public interface GGParser extends Parser {
    /**
     * Parses all the text files in directory
     *
     * @param path Path of the directory to parse
     * @return ArrayList of parsed Games
     * @throws IOException if something is wrong with file reading
     */
    public default ArrayList<Game> parseDirectoryFiles(String path)
            throws IOException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        File dir = new File(path);
        if (!dir.exists()) {
            throw new FileNotFoundException("Given path " + path + "could not have been found");
        }

        ArrayList<Game> allGames = new ArrayList<>();
        if (dir.isDirectory()) {
            if (dir.listFiles() == null) {
                return new ArrayList<>();
            }
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    addSubDirectoryGames(allGames, f);
                }
                if (f.isFile()) {
                    allGames.addAll(parseFile(f.getPath()));
                }
            }
        }
        // I need to log all the exceptions someway
        return allGames;
    }

    /**
     * @param allGames
     * @param dir
     * @throws IncorrectHandException
     * @throws IncorrectBoardException
     * @throws IOException
     * @throws IncorrectCardException
     */
    private void addSubDirectoryGames(ArrayList<Game> allGames, File dir) throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                addSubDirectoryGames(allGames, f);
            }
            if (f.isFile()) {
                allGames.addAll(parseFile(f.getPath()));
            }
        }
    }

    public default ArrayList<Game> parseFile(String path) throws IOException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        ArrayList<Game> parsedGames = new ArrayList<>();

        // try with resources
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader bfr = new BufferedReader(fr);

        String line = bfr.readLine();
        while (line != null) {
            StringBuilder gameText = new StringBuilder();
            // Getting to the first line of the game text.
            while (line != null && (line.equals("") || !line.substring(0, 5).equals("Poker"))) {
                line = bfr.readLine();
            }

            while (line != null && !line.equals("")) {
                gameText.append(line).append("\n");
                line = bfr.readLine();
            }
//            System.out.println(gameText.substring(0, 25));
            if (!gameText.toString().isEmpty()) {
                parsedGames.add(parseGame(gameText.toString()));
            }
        }
        return parsedGames;
    }
}
