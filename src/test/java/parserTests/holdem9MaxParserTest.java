package parserTests;

import analizer.GameAnalyzer;
import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
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
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/preflopUnusual/extraCashGames.txt");

        Game game = g.get(0);
        assertEquals(8, game.getPlayers().size());

        assertEquals(1, game.getPlayer("a3aa3c05").getSeatNumber());
        assertEquals(3, game.getPlayer("b858c98b").getSeatNumber());
        assertEquals(4, game.getPlayer("Hero").getSeatNumber());
        assertEquals(5, game.getPlayer("917be6aa").getSeatNumber());
        assertEquals(6, game.getPlayer("b1807748").getSeatNumber());


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

        assertEquals(2, game.getPlayer("68f87b81").getSeatNumber());
        assertEquals(3, game.getPlayer("b858c98b").getSeatNumber());
        assertEquals(4, game.getPlayer("Hero").getSeatNumber());
        assertEquals(5, game.getPlayer("2d6ccd2e").getSeatNumber());
        assertEquals(6, game.getPlayer("c4893b3c").getSeatNumber());
        assertEquals(7, game.getPlayer("f334408a").getSeatNumber());

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
        assertTrue(Math.abs(game.getWinners().get("Hero") - 2) < 0.0005);
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
        assertTrue(Math.abs(3.35 - game.getWinners().get("5ac5cf8c")) < 0.0005);

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
        assertTrue(Math.abs(game.getWinners().get("7c304922") - 1.75) < 0.0005);
        assertEquals(1, game.getWinners().size());

    }

    @Test
    public void testParsingShownHandsPreflopAllIn() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/showdownGame.txt");

        g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/preflopUnusual/straddledK7Game.txt");

        Game game = g.get(0);
        assertEquals(new Hand("Kd", "7s"), game.getPlayer("Hero").getHand());
        assertEquals(new Hand("Kc", "Js"), game.getPlayer("4d491e8a").getHand());
        assertEquals(new Hand("7d", "6d"), game.getPlayer("baa8df63").getHand());

        assertEquals(1, game.getWinners().size());
        assertTrue(Math.abs(102.52  - game.getWinners().get("4d491e8a")) < 0.0001);
    }

    @Test
    public void testHeroWinloss() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/showdownGame.txt");
        Game game = g.get(0);
        assertTrue(Math.abs(game.getHeroWinloss() - (-0.72)) < 0.005);

        g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/preflopUnusual/missedBlindsMultiWayGame.txt");
        game = g.get(0);
        assertTrue(Math.abs(game.getHeroWinloss() - (-0.15)) < 0.005);

        g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/preflopUnusual/superStraddledGame.txt");
        game = g.get(0);
        assertTrue(Math.abs(game.getHeroWinloss() - (-29.77)) < 0.005);
    }

    // region Unusual Preflop Actions

    @Test
    public void testParsingStraddledGame() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/preflopUnusual/superStraddledGame.txt");
        Game game = g.get(0);

        assertTrue(Math.abs(game.getPreFlop().getPotAfterBetting() - 79.94) < 0.005);

        ArrayList<Action> expectedActions = new ArrayList<>();
        expectedActions.add(new Action(ANTE,"9933cedb" ,0.05, 0));
        expectedActions.add(new Action(ANTE,"161e7c76" ,0.05, 0.05));
        expectedActions.add(new Action(ANTE,"290d7ea8" ,0.05, 0.10));
        expectedActions.add(new Action(ANTE,"Hero" ,0.05, 0.15));
        expectedActions.add(new Action(ANTE,"d0bfd2e1" ,0.05, 0.20));
        expectedActions.add(new Action(ANTE,"7221acc3" ,0.05, 0.25));
        expectedActions.add(new Action(ANTE,"2f7211df" ,0.05, 0.3));
        expectedActions.add(new Action(ANTE,"e04a92c4" ,0.05, 0.35));
        expectedActions.add(new Action(ANTE,"7b057d27" ,0.05, 0.4));

        expectedActions.add(new Action(BLIND, "161e7c76", 0.05, 0.45));
        expectedActions.add(new Action(BLIND, "7221acc3", 0.1, 0.5));

        expectedActions.add(new Action(STRADDLE, "Hero", 0.2, 0.6));
        expectedActions.add(new Action(STRADDLE, "161e7c76", 0.4, 0.8));
        expectedActions.add(new Action(STRADDLE, "161e7c76", 0.8, 1.15));
        expectedActions.add(new Action(STRADDLE, "161e7c76", 1.6, 1.55));

        expectedActions.add(new Action(FOLD, "7221acc3", 0, 2.35));
        expectedActions.add(new Action(RAISE, "d0bfd2e1", 19.95, 2.35));
        expectedActions.add(new Action(FOLD, "e04a92c4", 0, 22.3));
        expectedActions.add(new Action(FOLD, "7b057d27", 0, 22.3));
        expectedActions.add(new Action(FOLD, "2f7211df", 0, 22.3));
        expectedActions.add(new Action(FOLD, "290d7ea8", 0, 22.3));
        expectedActions.add(new Action(RAISE, "Hero", 62, 22.3));
        expectedActions.add(new Action(FOLD, "9933cedb", 0, 84.1));
        expectedActions.add(new Action(CALL, "161e7c76", 28.12, 84.1));


        assertEquals(expectedActions.size(), game.getPreFlop().getAllActions().size());
        for (int i = 0; i < game.getPreFlop().getAllActions().size(); ++i) {
            assertEquals(game.getPreFlop().getAllActions().get(i), expectedActions.get(i));
        }

        assertTrue(Math.abs(game.getPlayer("Hero").getBalance() - 74.99) < 0.005);
        assertTrue(Math.abs(game.getPlayer("161e7c76").getBalance() - 19.18) < 0.005);
        assertTrue(Math.abs(game.getPlayer("d0bfd2e1").getBalance() - 59.19) < 0.005);
        assertTrue(Math.abs(game.getPlayer("2f7211df").getBalance() - 24.82) < 0.005);
        assertTrue(Math.abs(game.getPlayer("7221acc3").getBalance() - 47.58) < 0.005);

        assertEquals(game.getPlayer("Hero").getHand(), new Hand("Js", "Jd"));
        assertEquals(game.getPlayer("161e7c76").getHand(), new Hand("As", "7c"));
        assertEquals(game.getPlayer("d0bfd2e1").getHand(), new Hand("Ac", "9d"));

        assertTrue(GameAnalyzer.isPot3Bet(game));
    }

    @Test
    public void testParsingMissedBlinds() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/preflopUnusual/missedBlindsMultiWayGame.txt");
        Game game = g.get(0);

       assertTrue(Math.abs(game.getFinalPot() - 1.72) < 0.005);

        ArrayList<Action> expectedActions = new ArrayList<>();
        expectedActions.add(new Action(ANTE,"4d83f902" ,0.05, 0));
        expectedActions.add(new Action(ANTE,"271d9d85" ,0.05, 0.05));
        expectedActions.add(new Action(ANTE,"Hero" ,0.05, 0.10));
        expectedActions.add(new Action(ANTE,"21bd2a49", 0.05, 0.15));
        expectedActions.add(new Action(ANTE,"97989988" ,0.05, 0.20));
        expectedActions.add(new Action(ANTE,"140e2d1d" ,0.05, 0.25));

        expectedActions.add(new Action(BLIND, "271d9d85", 0.05, 0.3));
        expectedActions.add(new Action(BLIND, "140e2d1d", 0.1, 0.35));
        expectedActions.add(new Action(BLIND, "97989988", 0.1, 0.45));
        expectedActions.add(new Action(MISSED_BLIND, "97989988", 0.05, 0.55));

        expectedActions.add(new Action(CALL,"4d83f902",0.1, 0.6));
        expectedActions.add(new Action(FOLD,"21bd2a49",0, 0.7));
        expectedActions.add(new Action(CHECK, "97989988", 0, 0.7));
        expectedActions.add(new Action(CALL,"Hero",0.1, 0.7));
        expectedActions.add(new Action(CALL,"271d9d85",0.05, 0.8));
        expectedActions.add(new Action(CHECK,"140e2d1d",0, 0.85));

        assertEquals(expectedActions.size(), game.getPreFlop().getAllActions().size());
        for (int i = 0; i < game.getPreFlop().getAllActions().size(); ++i) {
            assertEquals(game.getPreFlop().getAllActions().get(i), expectedActions.get(i));
        }

        assertTrue(Math.abs(game.getPreFlop().getPotAfterBetting() - 0.85) < 0.005);
        assertTrue(Math.abs(game.getWinners().get("97989988") - 1.64) < 0.005);
        assertTrue(Math.abs(game.getPlayerWinloss("97989988") - 1.15) <0.005);

        assertTrue(Math.abs(game.getHeroWinloss() - (-0.15)) < 0.005);

    }

    @Test
    public void testHeroMissedBlinds() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        Parser parser = new GGPokerokHoldem9MaxParser();
        ArrayList<Game> g = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/preflopUnusual/heroMissedBlindsNoSitout.txt");
        Game game = g.get(0);

        ArrayList<Action> expectedActions = new ArrayList<>();
        expectedActions.add(new Action(ANTE,"fd181360" ,0.05, 0));
        expectedActions.add(new Action(ANTE,"47bec60f" ,0.05, 0.05));
        expectedActions.add(new Action(ANTE,"Hero" ,0.05, 0.10));
        expectedActions.add(new Action(ANTE,"7e8ea7f5", 0.05, 0.15));
        expectedActions.add(new Action(ANTE,"92639c3b" ,0.05, 0.20));
        expectedActions.add(new Action(ANTE,"a9b0a723" ,0.05, 0.25));
        expectedActions.add(new Action(ANTE,"69dc8349" ,0.05, 0.3));

        expectedActions.add(new Action(BLIND, "a9b0a723", 0.05, 0.35));
        expectedActions.add(new Action(BLIND, "47bec60f", 0.1, 0.4));
        expectedActions.add(new Action(BLIND, "Hero", 0.1, 0.5));

        expectedActions.add(new Action(RAISE, "Hero", 0.4, 0.6));
        expectedActions.add(new Action(FOLD, "69dc8349", 0, 0.9));
        expectedActions.add(new Action(RAISE, "fd181360", 1.2, 0.9));
        expectedActions.add(new Action(FOLD, "7e8ea7f5", 0, 2.1));
        expectedActions.add(new Action(RAISE, "92639c3b", 3.62, 2.1));
        expectedActions.add(new Action(FOLD, "a9b0a723", 0, 5.72));
        expectedActions.add(new Action(FOLD, "47bec60f", 0, 5.72));
        expectedActions.add(new Action(FOLD, "Hero", 0, 5.72));
        expectedActions.add(new Action(CALL, "fd181360", 2.42, 5.72));


//        assertEquals(expectedActions.size(), game.getPreFlop().getAllActions().size());
        for (int i = 0; i < game.getPreFlop().getAllActions().size(); ++i) {
            assertEquals(game.getPreFlop().getAllActions().get(i), expectedActions.get(i));
        }

        assertTrue(Math.abs(game.getHeroWinloss() - (-0.45)) < 0.0005);
        assertTrue(GameAnalyzer.isPot4Bet(game));
    }
    // endregion





}
