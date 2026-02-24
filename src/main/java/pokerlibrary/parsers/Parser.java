package pokerlibrary.parsers;

import pokerlibrary.exceptions.IncorrectBoardException;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.exceptions.IncorrectHandException;
import pokerlibrary.models.Game;

import java.io.IOException;
import java.util.ArrayList;

public interface Parser {
    Game parseGame(String gameText) throws IncorrectCardException, IncorrectHandException, IncorrectBoardException;

    ArrayList<Game> parseFile(String path) throws IOException, IncorrectHandException, IncorrectBoardException, IncorrectCardException;

    ArrayList<Game> parseDirectoryFiles(String path) throws IOException, IncorrectHandException, IncorrectBoardException, IncorrectCardException;
}
