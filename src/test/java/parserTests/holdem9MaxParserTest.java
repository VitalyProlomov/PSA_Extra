package parserTests;

import analizer.GameAnalyzer;
import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import javafx.geometry.Pos;
import models.*;
import org.junit.jupiter.api.Test;
import parsers.Parser;
import parsers.gg.GGPokerokHoldem9MaxParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static models.Action.ActionType.*;
import static models.PositionType.*;
import static org.junit.jupiter.api.Assertions.*;

public class holdem9MaxParserTest {
    @Test
    public void testParsingExtraCashGame() throws IncorrectHandException, IncorrectBoardException, IncorrectCardException, IOException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        String path = "";
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/extraCashGames.txt");

        Game game = g.get(0);
        assertEquals(8, game.getPlayers().size());

        assertEquals(SB, game.getPlayer("Hero").getPosition());
        assertEquals(BTN, game.getPlayer("b858c98b").getPosition());
        assertEquals(UTG, game.getPlayer("b1807748").getPosition());

        assertTrue(Math.abs(16.51 - game.getInitialBalances().get("f334408a")) < 0.005);
        assertTrue(Math.abs(23.06 - game.getInitialBalances().get("eb8f3f2f")) < 0.005);
        assertTrue(Math.abs(5.12 - game.getInitialBalances().get("917be6aa")) < 0.005);

        assertTrue(Math.abs(4.97 - game.getPlayer("917be6aa").getBalance()) < 0.005);
        assertTrue(Math.abs(23.56 - game.getPlayer("eb8f3f2f").getBalance()) < 0.005);
        assertTrue(Math.abs(22.22 - game.getPlayer("b1807748").getBalance()) < 0.005);
        assertTrue(Math.abs(game.getInitialBalances().get("Hero") - game.getPlayer("Hero").getBalance() - 0.10) < 0.005);
        assertEquals(new Hand(new Card("Qs"), new Card("Jd")),
                game.getPlayer("Hero").getHand());
        assertNull(game.getFlop());

        game = g.get(1);
        assertEquals(6, game.getPlayers().size());
        assertEquals(UTG_2, game.getPlayer("Hero").getPosition());
        assertEquals(BB, game.getPlayer("f334408a").getPosition());
        assertEquals(UTG_1, game.getPlayer("b858c98b").getPosition());

        assertEquals(45.43, game.getInitialBalances().get("68f87b81"));
        assertEquals(56.97, game.getInitialBalances().get("2d6ccd2e"));
        assertEquals(10.86, game.getInitialBalances().get("f334408a"));

        assertEquals(0.69 + 56.97, game.getPlayer("2d6ccd2e").getBalance());
        assertEquals(10.86 - 0.05 - 0.45, game.getPlayer("f334408a").getBalance());
        assertEquals(29.7 - 0.05, game.getPlayer("Hero").getBalance());

        StreetDescription flop = new StreetDescription();
        flop.setBoard(new Board("3s", "5d", "Jh"));
        flop.setPotAfterBetting(1.19);
        ArrayList<PlayerInGame> pl = new ArrayList<>();
        pl.add(new PlayerInGame("2d6ccd2e"));
        flop.setPlayersAfterBetting(pl);

        assertEquals(new Action(CHECK, "f334408a", 0, 1.25), game.getFlop().getAllActions().get(0));
        assertEquals(new Action(BET, "2d6ccd2e", 0.42, 1.25), game.getFlop().getAllActions().get(1));
        assertEquals(new Action(FOLD, "f334408a", 0, 1.67), game.getFlop().getAllActions().get(2));

        assertEquals(flop.getPlayersAfterBetting(), game.getFlop().getPlayersAfterBetting());
        assertEquals(flop.getBoard(), game.getFlop().getBoard());
        assertEquals(0.06, game.getRake());
        assertEquals(flop.getPotAfterBetting() + game.getRake(), game.getFlop().getPotAfterBetting());
    }

    @Test
    public void testParse3betPotGame() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        String path = "";
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/game3BetPot.txt");

        Game game = g.get(0);
        assertEquals(4, game.getPlayers().size());
        HashMap<String, Double> expInitB = new HashMap<>();
        expInitB.put("Hero", 16.73);
        expInitB.put("f1ba23db", 12.29);
        expInitB.put("fdeaae24", 8.75);
        expInitB.put("da52607b", 31.92);

        for (String hash: expInitB.keySet()) {
            assertNotNull(game.getInitialBalances().get(hash));
            assertEquals(expInitB.get(hash), game.getInitialBalances().get(hash));
        }

        assertEquals(game.getPlayer("Hero").getPosition(), SB);
        assertEquals(game.getPlayer("fdeaae24").getPosition(), BB);
        assertEquals(game.getPlayer("f1ba23db").getPosition(), BTN);
        assertEquals(game.getPlayer("da52607b").getPosition(), UTG);

        assertEquals(game.getPlayer("Hero").getHand(), new Hand("Ah", "5h"));

        assertTrue(Math.abs(17.78 -  game.getPlayer("Hero").getBalance()) < 0.0005);
        assertTrue(Math.abs(8.6 -  game.getPlayer("fdeaae24").getBalance()) < 0.0005);
        assertTrue(Math.abs(31.87 -  game.getPlayer("da52607b").getBalance()) < 0.0005);
        assertTrue(Math.abs(11.34 -  game.getPlayer("f1ba23db").getBalance()) < 0.0005);

        assertTrue(GameAnalyzer.isPlayer3BetCaller(game, "f1ba23db"));
        assertTrue(GameAnalyzer.isPot3Bet(game));
        assertTrue(GameAnalyzer.isPlayer3BetRaiser(game, "Hero"));

        assertTrue(Math.abs(game.getHeroWinloss() - 1.05) < 0.005);
        assertTrue(Math.abs(game.getWinners().get("Hero") - 1.05) < 0.0005);
        assertEquals(1, game.getWinners().size());

        assertEquals(game.getTurn().getBoard(), new Board("Jd", "3c", "2c", "5d"));
        assertTrue(Math.abs(game.getTurn().getPotAfterBetting() - 2.1) < 0.0005);
    }

    @Test
    public void testPlayersPositions() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/showdownGame.txt");

        Game game = g.get(0);
        HashMap<String, PositionType> posMap = new HashMap<>();
        posMap.put("6e1e5fb6", BTN);
        posMap.put("76855856", SB);
        posMap.put("Hero", BB);
        posMap.put("7c304922", UTG);
        posMap.put("2e5595a", UTG_1);
        posMap.put("57d8cf46", UTG_2);
        posMap.put("d783b710", LJ);
        posMap.put("5ac5cf8c", HJ);
        posMap.put("52743c37", CO);

        for (String hash : posMap.keySet()) {
            assertEquals(posMap.get(hash), game.getPlayer(hash).getPosition());
        }

        g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/turnFoldedGame.txt");
        game = g.get(0);

        posMap = new HashMap<>();
        posMap.put("7c304922", BTN);
        posMap.put("2e5595a", SB);
        posMap.put("57d8cf46", BB);
        posMap.put("d783b710", UTG);
        posMap.put("5ac5cf8c", UTG_1);
        posMap.put("52743c37", UTG_2);
        posMap.put("6e1e5fb6", LJ);
        posMap.put("76855856", HJ);
        posMap.put("Hero", CO);

        for (String hash : posMap.keySet()) {
            assertEquals(posMap.get(hash), game.getPlayer(hash).getPosition());
        }

    }

    @Test
    public void testParsingMultiWayGame() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/turnFoldedGame.txt");

        Game game = g.get(0);
        assertEquals(3, game.getPreFlop().getPlayersAfterBetting().size());
        assertEquals(1, game.getWinners().size());
        assertTrue(Math.abs(2.3 - game.getWinners().get("5ac5cf8c")) < 0.0005);

        assertTrue(Math.abs(game.getHeroWinloss() - (-0.05)) < 0.0005);

        assertTrue(Math.abs(game.getRake() - 0.25) < 0.05);
    }

    @Test
    public void testParsingShownHands() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/showdownGame.txt");

        Game game = g.get(0);

        assertEquals(new Hand("Td", "9d"), game.getPlayer("Hero").getHand());
        assertEquals(new Hand("Ac", "Jd"), game.getPlayer("7c304922").getHand());

        assertTrue(Math.abs(game.getHeroWinloss() - (-0.72)) < 0.0005);
        assertTrue(Math.abs(game.getWinners().get("7c304922") - 1.03) < 0.0005);
        assertEquals(1, game.getWinners().size());
    }

    @Test
    public void testHeroWinloss() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/showdownGame.txt");
        Game game = g.get(0);
        assertTrue(Math.abs(game.getHeroWinloss() - (-0.72)) < 0.005);

        g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/missedBlindsMultiWayGame.txt");
        game = g.get(0);
//        assertTrue();



    }
}
