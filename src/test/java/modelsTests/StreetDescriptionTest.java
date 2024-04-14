package modelsTests;

import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Game;
import org.junit.jupiter.api.Test;
import parsers.Parser;
import parsers.gg.GGPokerokRushNCashParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreetDescriptionTest {
    @Test
    public void testGetLastAggressor() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertEquals("a7067c39", game.getPreFlop().getLastAggressorHash());

        path ="src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt";
        game = parser.parseFile(path).get(0);
        assertEquals("480564b2", game.getFlop().getLastAggressorHash());

        // RC1224871300
        path ="src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gameSession2.txt";
        game = parser.parseFile(path).get(2); // 2.
        assertEquals("4230c35e", game.getFlop().getLastAggressorHash());

        path ="src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/allInTwoRunoutsGame.txt";
        game = parser.parseFile(path).get(0);
        assertEquals("820e8a4", game.getPreFlop().getLastAggressorHash());

        path ="src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/problemGame.txt";
        game = parser.parseFile(path).get(0);
        assertEquals("d579d6ed", game.getTurn().getLastAggressorHash());
    }
}
