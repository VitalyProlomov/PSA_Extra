package modelsTests;

import pokerlibrary.analizer.GameAnalyzer;
import pokerlibrary.exceptions.IncorrectBoardException;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.exceptions.IncorrectHandException;
import pokerlibrary.models.Game;
import pokerlibrary.models.PlayerInGame;
import org.junit.jupiter.api.Test;
import pokerlibrary.parsers.Parser;
import pokerlibrary.parsers.gg.GGPokerokRushNCashParser;
import pokerlibrary.utils.Money;

import java.io.IOException;
import java.util.HashMap;

import static pokerlibrary.models.PositionType.BTN;
import static pokerlibrary.models.PositionType.CO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
    @Test
    public void testGameGetSB() {
        Game game = new Game("RC3", new Money("0.02"), null, null);
        assertEquals(0.01, game.getSB());

        game = new Game("RC3", new Money("0.05"), null, null);
        assertTrue(game.getSB().subtract(new Money("0.02")).isZero());

        game = new Game("RC1234567", new Money("0.25"), null, null);
        assertEquals(0.1, game.getSB());

        game = new Game("RC3", new Money("0.5"), null, null);
        assertEquals(0.25, game.getSB());

        game = new Game("RC4", new Money("1"), null, null);
        assertEquals(0.5, game.getSB());
    }

    @Test
    public void testDecrementBalance() {
        HashMap<String, Money> initBalances = new HashMap<>();
        HashMap<String, PlayerInGame> playersMap = new HashMap<>();
        playersMap.put("player1", new PlayerInGame("player1", CO, new Money("1000")));
        playersMap.put("player2",  new PlayerInGame("player2", BTN, new Money("960")));

        initBalances.put("player1", new Money("1000.0"));
        initBalances.put("player2", new Money("960.0"));

        Game game = new Game("Test", new Money("10"), playersMap, initBalances);

        game.decrementPlayersBalance("player1", new Money("50"));
        assertEquals(950, game.getPlayer("player1").getBalance());

        assertEquals(game.getInitialBalances(), initBalances);
    }

    @Test
    public void testGetHeroWinLoss() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/preFlopAllInExtraCashGame.txt";
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        Game game = parser.parseFile(path).get(0);

        assertTrue(new Money("7.75").subtract(game.getHeroWinloss()).isZero() );
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
