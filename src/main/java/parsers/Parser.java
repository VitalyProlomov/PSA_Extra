package parsers;

import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Game;

import java.io.IOException;
import java.util.ArrayList;

public interface Parser {
    public Game parseGame(String gameText) throws IncorrectCardException, IncorrectHandException, IncorrectBoardException;

    public ArrayList<Game> parseFile(String path) throws IOException, IncorrectHandException, IncorrectBoardException, IncorrectCardException;

    public ArrayList<Game> parseDirectoryFiles(String path) throws IOException, IncorrectHandException, IncorrectBoardException, IncorrectCardException;
}
