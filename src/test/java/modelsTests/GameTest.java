package modelsTests;

import analizer.GameAnalyzer;
import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Game;
import models.PlayerInGame;
import org.junit.jupiter.api.Test;
import parsers.Parser;
import parsers.gg.GGPokerokRushNCashParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static models.PositionType.BTN;
import static models.PositionType.CO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
    @Test
    public void testGameGetSB() {
        Game game = new Game("RC3", 0.02, null, null);
        assertEquals(game.getSB(), 0.01);

        game = new Game("RC3", 0.05, null, null);
        assertTrue(Math.abs(game.getSB() - 0.02) < 0.001);

        game = new Game("RC1234567", 0.25, null, null);
        assertEquals(game.getSB(), 0.1);

        game = new Game("RC3", 0.5, null, null);
        assertEquals(game.getSB(), 0.25);

        game = new Game("RC4", 1, null, null);
        assertEquals(game.getSB(), 0.5);
    }

    @Test
    public void testDecrementBalance() {
        HashSet<PlayerInGame> players = new HashSet<>(List.of(
                new PlayerInGame("player1", CO, 1000),
                new PlayerInGame("player2", BTN, 960)
        ));
        HashMap<String, Double> initBalances = new HashMap<>();
        HashMap<String, PlayerInGame> playersMap = new HashMap<>();
        playersMap.put("player1", new PlayerInGame("player1", CO, 1000));
        playersMap.put("player2",  new PlayerInGame("player2", BTN, 960));

        initBalances.put("player1", 1000.0);
        initBalances.put("player2", 960.0);

        Game game = new Game("Test", 10, playersMap, initBalances);

        game.decrementPlayersBalance("player1", 50);
        assertEquals(game.getPlayer("player1").getBalance(), 950);

        assertEquals(game.getInitialBalances(), initBalances);
    }

    @Test
    public void testGetHeroWinLoss() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/preFlopAllInExtraCashGame.txt";
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        Game game = parser.parseFile(path).get(0);

        assertTrue(Math.abs(7.75 - game.getHeroWinloss()) < 0.005);
    }

    @Test
    public void testGetPFRHash() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertEquals("a7067c39", GameAnalyzer.getPFRHash(game));

        path ="src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt";
        game = parser.parseFile(path).get(0);
        assertEquals("480564b2", GameAnalyzer.getPFRHash(game));

        // RC1224871300
        path ="src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gameSession2.txt";
        game = parser.parseFile(path).get(2); // 2.
        assertEquals("3e24ccf", GameAnalyzer.getPFRHash(game));

        path ="src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/allInTwoRunoutsGame.txt";
        game = parser.parseFile(path).get(0);
        assertEquals("820e8a4", GameAnalyzer.getPFRHash(game));
    }
}
