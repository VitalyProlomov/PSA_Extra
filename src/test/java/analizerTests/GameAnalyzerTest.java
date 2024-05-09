package analizerTests;

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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameAnalyzerTest {


    @Test
    public void testIsHeroSRPCaller() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        // Hero has folded
        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.isPlayerSRPC(game, "Hero"));

        // Hero is 3bC
        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt";
        game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.isPlayerSRPC(game, "Hero"));

        // Hero is srpR
        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gamesForAnalyzer/heroSRPR.txt";
        game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.isPlayerSRPC(game, "Hero"));

        // Hero is limper
        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gamesForAnalyzer/heroLimper.txt";
        game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.isPlayerSRPC(game, "Hero"));

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gamesForAnalyzer/heroSRPC.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayerSRPC(game, "Hero"));

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gameExample.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayerSRPC(game, "Hero"));
    }


    @Test
    public void testIsPlayerPFR() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayerPFR(game, "a7067c39"));

        // Hero is 3bC
        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayerPFR(game, "480564b2"));
        assertFalse(GameAnalyzer.isPlayerPFR(game, "Hero"));
        assertFalse(GameAnalyzer.isPlayerPFR(game, "aaA"));

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gamesForAnalyzer/heroSRPR.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayerPFR(game, "Hero"));

        // Limped pot
        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gamesForAnalyzer/heroLimper.txt";
        game = parser.parseFile(path).get(0);
        for (PlayerInGame p : game.getPlayers().values()) {
            assertFalse(GameAnalyzer.isPlayerPFR(game, p.getId()));
        }
    }

    @Test
    public void testIsPlayer3BetRaiser() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        // no 3bet at all/
        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.isPlayer3BetRaiser(game, "a7067c39"));
        assertFalse(GameAnalyzer.isPlayer3BetRaiser(game, "ea0c9d4e"));

        // 3bet raiser is in game.
        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayer3BetRaiser(game, "480564b2"));
        assertFalse(GameAnalyzer.isPlayer3BetRaiser(game, "Hero"));

        // Hero 4bet => no one can be 3bettor (because in that method final role of
        // the player is checked).
        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gamesForAnalyzer/hero4bR.txt";
        game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.isPlayer3BetRaiser(game, "Hero"));
        assertFalse(GameAnalyzer.isPlayer3BetRaiser(game, "ca237476"));
        assertFalse(GameAnalyzer.isPlayer3BetRaiser(game, "1be54de8"));
    }

    @Test
    public void testDidPlayer3bet() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.didPlayer3Bet(game, "Hero"));
        assertTrue(GameAnalyzer.didPlayer3Bet(game, "480564b2"));

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gamesForAnalyzer/hero4bR.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.didPlayer3Bet(game, "ca237476"));
        assertFalse(GameAnalyzer.didPlayer3Bet(game, "Hero"));
        assertFalse(GameAnalyzer.didPlayer3Bet(game, "1be54de8"));

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/balanceGameTest.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.didPlayer3Bet(game, "Hero"));
        assertFalse(GameAnalyzer.didPlayer3Bet(game, "2188c01f"));
        assertFalse(GameAnalyzer.didPlayer3Bet(game, "184e50ef"));

    }

    @Test
    public void testIsPlayer3BetCaller() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayer3BetCaller(game, "Hero"));
        assertFalse(GameAnalyzer.isPlayer3BetCaller(game, "480564b2"));


        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/returningChipsToBalanceGame.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayer3BetCaller(game, "b0f7d5b6"));
        assertFalse(GameAnalyzer.isPlayer3BetCaller(game, "2188c01f"));
        assertFalse(GameAnalyzer.isPlayer3BetCaller(game, "Hero"));
        assertFalse(GameAnalyzer.isPlayer3BetCaller(game, "c4d1d5f5"));

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/returningChipsToBalanceGame.txt";
        game = parser.parseFile(path).get(0);
        assertTrue(GameAnalyzer.isPlayer3BetCaller(game, "b0f7d5b6"));

    }

    @Test
    public void testIsFlopCheckRaisedByCaller() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokRushNCashParser();

        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt";
        Game game = parser.parseFile(path).get(0);
        assertFalse(GameAnalyzer.isFlopCheckRaisedByCaller(game));

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/checkRaisingTestsGames/flopCheckRaisedTwoPlayersGames.txt";
        ArrayList<Game> twoPlayersCheckRaiseGames = parser.parseFile(path);
        for (Game g : twoPlayersCheckRaiseGames) {
            assertTrue(GameAnalyzer.isFlopCheckRaisedByCaller(g));
        }

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/checkRaisingTestsGames/falseCheckRaisingGames.txt";
        ArrayList<Game> falseCheckRaiseGames = parser.parseFile(path);
        for (Game g : falseCheckRaiseGames) {
            assertFalse(GameAnalyzer.isFlopCheckRaisedByCaller(g));
        }

        path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/gameSession2.txt";
        ArrayList<Game> games = parser.parseFile(path);
    }



}
