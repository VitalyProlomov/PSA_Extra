package parserTests;

import analizer.GameAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.*;
import org.junit.jupiter.api.Test;
import parsers.gg.GGPokerokRushNCashParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static models.Action.ActionType.*;
import static models.PositionType.*;
import static org.junit.jupiter.api.Assertions.*;

public class rushNCashParsingTest {
    public ArrayList<PositionType> orderedPositions =
            new ArrayList<>(List.of(SB, BB, LJ, HJ, CO, BTN));

    private String getTextFromFile(String path) throws FileNotFoundException {
        URL gameURL = rushNCashParsingTest.class.getResource(path);
        assert gameURL != null;
        FileReader fr = new FileReader(gameURL.getFile().replace("%20", " "));
        Scanner scanner = new Scanner(fr);

        StringBuilder gameText = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            gameText.append(line).append("\n");
        }
        return gameText.toString();
    }

    @Test
    public void testFullGameInfoSplitting() throws IOException, IncorrectCardException, IncorrectHandException, IncorrectBoardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();

        Game topG = parser.parseGame(getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/gameExample.txt"));

        String correctID = "RC1221829603";
        assertEquals(correctID, topG.getGameId());

        double correctBbSize = 0.25;
        assertEquals(correctBbSize, topG.getBigBlindSize$());

        Date date = new Date("2022/12/15 21:35:07");
        assertEquals(date, topG.getDate());

        ArrayList<PlayerInGame> correctPlayers = new ArrayList<>(List.of(
                new PlayerInGame("96112e6e", BTN, 42.6),
                new PlayerInGame("bdfdf5d3", SB, 35.23),
                new PlayerInGame("Hero", PositionType.BB, 58.12),
                new PlayerInGame("906a880b", PositionType.LJ, 148.44),
                new PlayerInGame("40b398d7", PositionType.HJ, 30.35),
                new PlayerInGame("805c6855", CO, 21.99)
        ));

        assertEquals(new HashSet<>(correctPlayers), new HashSet<>(topG.getPlayers().values()));

        Hand heroHand = new Hand(new Card("Ad"), new Card("Ts"));
        correctPlayers.get(2).setHand(heroHand);
        assertEquals(topG.getPosPlayersMap().get(PositionType.BB).getHand(), new Hand(heroHand));

        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(BLIND, correctPlayers.get(1).getId(), 0.1, 0));
        actions.add(new Action(BLIND, correctPlayers.get(2).getId(), 0.25, 0.1));
        actions.add(new Action(Action.ActionType.FOLD, correctPlayers.get(3).getId(), 0, 0.35));
        actions.add(new Action(Action.ActionType.FOLD, correctPlayers.get(4).getId(), 0, 0.35));
        actions.add(new Action(Action.ActionType.RAISE, correctPlayers.get(5).getId(), 0.63, 0.35));
        actions.add(new Action(Action.ActionType.FOLD, correctPlayers.get(0).getId(), 0, 0.98));
        actions.add(new Action(Action.ActionType.FOLD, correctPlayers.get(1).getId(), 0, 0.98));
        actions.add(new Action(Action.ActionType.CALL, correctPlayers.get(2).getId(), 0.38, 0.98));

        ArrayList<PlayerInGame> left = new ArrayList<>(List.of(correctPlayers.get(2), correctPlayers.get(5)));
        StreetDescription correctPreFlop = new StreetDescription(1.36, null, left, actions);
        //for (PositionType pos : orderedPositions) {
        assertEquals(correctPreFlop, topG.getPreFlop());

        actions = new ArrayList<>();
        actions.add((new Action(Action.ActionType.CHECK, correctPlayers.get(2).getId(), 0, 1.36)));
        actions.add((new Action(Action.ActionType.CHECK, correctPlayers.get(5).getId(), 0, 1.36)));

        StreetDescription correctFLop = new StreetDescription(1.36,
                new Board("8d", "6d", "Qh"),
                new ArrayList<PlayerInGame>(List.of(
                        correctPlayers.get(2), correctPlayers.get(5))), actions);

        correctFLop.setPlayersAfterBetting(new ArrayList<>(
                List.of(correctPlayers.get(2), correctPlayers.get(5))));

        assertEquals(topG.getFlop(), correctFLop);

        actions = new ArrayList<>(List.of(new Action(Action.ActionType.CHECK,
                        correctPlayers.get(2).getId(), 0, 1.36),
                new Action(Action.ActionType.CHECK, correctPlayers.get(5).getId(), 0, 1.36)));

        StreetDescription correctTurn = new StreetDescription(1.36,
                new Board("8d", "6d", "Qh", "4s"),
                new ArrayList<>(List.of(correctPlayers.get(2), correctPlayers.get(5))), actions);

        assertEquals(correctTurn, topG.getTurn());

        actions = new ArrayList<>(List.of(
                new Action(Action.ActionType.CHECK, correctPlayers.get(2).getId(), 0, 1.36),
                new Action(Action.ActionType.CHECK, correctPlayers.get(5).getId(), 0, 1.36))
        );

        StreetDescription correctRiver = new StreetDescription(
                1.36,
                new Board("8d", "6d", "Qh", "4s", "9s"),
                new ArrayList<>(List.of(correctPlayers.get(2), correctPlayers.get(5))),
                actions
        );

        assertEquals(correctRiver, topG.getRiver());

        double finalPot = 1.36;
        double rake = 0.06;

        PlayerInGame winner = new PlayerInGame(correctPlayers.get(5));

//        assertEquals(winner, topG.getWinner());
        assertTrue(Math.abs(finalPot - topG.getFinalPot()) < 0.01);
//        assertEquals(rake, topG.getRake());
    }

    // region Special Games Parsing
    @Test
    public void testEarlyAllinGameParsing()
            throws FileNotFoundException, IncorrectHandException,
            IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String text = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/earlyAllInCashoutGame.txt");

        Game topG = parser.parseGame(text);

        assertEquals("RC1281883052", topG.getGameId());
        assertEquals(new Date("2023/01/15 20:20:28"), topG.getDate());
        assertEquals(0.05, topG.getBigBlindSize$());

        ArrayList<PlayerInGame> correctPlayers = new ArrayList<>(List.of(
                new PlayerInGame("c491325f", BTN, 6.12),
                new PlayerInGame("Hero", SB, 13.4),
                new PlayerInGame("16d75d78", BB, 6.28),
                new PlayerInGame("c1f81489", LJ, 10.52),
                new PlayerInGame("361dc4bb", HJ, 2.31),
                new PlayerInGame("219f215e", CO, 7.88))
        );

        for (int i = 0; i < 6; ++i) {
            assertNotNull(topG.getPlayers().get(correctPlayers.get(i).getId()));
            assertEquals(correctPlayers.get(i).getPosition(),
                    topG.getPlayer(correctPlayers.get(i).getId()).getPosition());
        }

        assertEquals(12.09, topG.getPlayer("c491325f").getBalance());
        assertEquals(7.75 - 5.99, topG.getPlayer("219f215e").getBalance());

        Hand heroHand = new Hand("3s", "9d");
        assertEquals(heroHand, topG.getPlayer("Hero").getHand());

        ArrayList<Action> preFlopActions = new ArrayList<>(List.of(
                new Action(BLIND, "Hero", 0.02, 0),
                new Action(BLIND, "16d75d78", 0.05, 0.02),
                new Action(FOLD, "c1f81489", 0, 0.07),
                new Action(FOLD, "361dc4bb", 0, 0.07),
                new Action(RAISE, "219f215e", 0.13, 0.07),
                new Action(CALL, "c491325f", 0.13, 0.2),
                new Action(FOLD, "Hero", 0, 0.33),
                new Action(FOLD, "16d75d78", 0, 0.33))
        );
        assertEquals(preFlopActions, topG.getPreFlop().getAllActions());

        // Flop assertions
        ArrayList<Action> flopActions = new ArrayList<>(List.of(
                new Action(BET, "219f215e", 0.2, 0.33),
                new Action(RAISE, "c491325f", 5.99, 0.53),
                new Action(CALL, "219f215e", 5.79, 6.52)
        ));

        assertEquals(12.31, topG.getFlop().getPotAfterBetting());
        assertEquals(flopActions, topG.getFlop().getAllActions());
        assertTrue(topG.getFlop().getPlayersAfterBetting().contains(new PlayerInGame("c491325f")));
        assertTrue(topG.getFlop().getPlayersAfterBetting().contains(new PlayerInGame("219f215e")));
        assertEquals(2, topG.getFlop().getPlayersAfterBetting().size());
        assertEquals(new Board("Ks", "5s", "3c"), topG.getFlop().getBoard());

        assertNull(topG.getTurn());
        assertNull(topG.getRiver());
        //Decided not to add winners to the game for now
        // assertEquals(correctPlayers.get(0), topG.getWinner());
//        assertEquals(12.31, topG.getFinalPot());
//        assertEquals(0.22, topG.getRake());
        assertEquals(new Hand("5h", "5d"), topG.getPlayer("c491325f").getHand());
        assertEquals(new Hand("Ah", "Kh"), topG.getPlayer("219f215e").getHand());
    }

    @Test
    public void testPreFlopAllInExtraCashGameParsing() throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String text = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/preFlopAllInExtraCashGame.txt");

        Game topG = parser.parseGame(text);
        assertEquals("RC1281882663", topG.getGameId());
        assertEquals(0.05, topG.getBigBlindSize$());
        assertEquals(new Date("2023/01/15 20:19:04"), topG.getDate());

        Set<PlayerInGame> players = new HashSet<>(List.of(
                new PlayerInGame("a119ad3f", BTN, 5),
                new PlayerInGame("81a0604b", SB, 3.56, null),
                new PlayerInGame("486d3078", BB, 1.41, null),
                new PlayerInGame("Hero", LJ, 13.61 - topG.getRake(), null),
                new PlayerInGame("d67af16c", HJ, 9.3, null),
                new PlayerInGame("c4d36ec9", CO, 0, null)
        ));
        assertEquals(players, new HashSet<>(topG.getPlayers().values()));
        for (PlayerInGame p : players) {
            assertTrue(p.getBalance() - topG.getPlayer(p.getId()).getBalance() < 0.01);
            assertEquals(p.getPosition(), topG.getPlayer(p.getId()).getPosition());
        }

        assertEquals(0.5, topG.getExtraCashAmount());
        assertEquals(new Hand("Ac", "Ah"), topG.getPlayer("Hero").getHand());

        ArrayList<Action> actions = new ArrayList<>(List.of(
                new Action(BLIND, "81a0604b", 0.02, 0.5),
                new Action(BLIND, "486d3078", 0.05, 0.52),
                new Action(CALL, "Hero", 0.05, 0.57),
                new Action(FOLD, "d67af16c", 0, 0.62),
                new Action(RAISE, "c4d36ec9", 0.15, 0.62),
                new Action(FOLD, "a119ad3f", 0, 0.77),
                new Action(FOLD, "81a0604b", 0, 0.77),
                new Action(RAISE, "486d3078", 7.06, 0.77),
                new Action(CALL, "Hero", 5.6, 7.78),
                new Action(CALL, "c4d36ec9", 1.64, 13.38)
        ));
        assertEquals(actions, topG.getPreFlop().getAllActions());

        Hand h1 = new Hand("Td", "Ts");
        Hand h2 = new Hand("Tc", "Th");
        Hand heroHand = new Hand("Ac", "Ah");
        assertEquals(h1, topG.getPlayer("486d3078").getHand());
        assertEquals(h2, topG.getPlayer("c4d36ec9").getHand());
        assertEquals(heroHand, topG.getPlayer("Hero").getHand());
        assertTrue(topG.getPreFlop().isAllIn());

        assertNull(topG.getFlop());
        assertNull(topG.getTurn());
        assertNull(topG.getRiver());

        // Test final pot and rake

    }

    @Test
    public void testHandsShownGameParsing() throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String text = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/handShownGame.txt");

        Game topG = parser.parseGame(text);
        assertEquals("RC1292105656", topG.getGameId());
        assertEquals(0.05, topG.getBigBlindSize$());
        assertEquals(new Date("2023/01/20 23:31:52"), topG.getDate());

        Set<PlayerInGame> players = new HashSet<>(List.of(
                new PlayerInGame("1e9152a8", BTN, 9.41),
                new PlayerInGame("Hero", SB, 7.28, null),
                new PlayerInGame("480564b2", BB, 5.90, null),
                new PlayerInGame("59435a5e", LJ, 2.02, null),
                new PlayerInGame("dd1dce69", HJ, 5.49, null),
                new PlayerInGame("363e9d13", CO, 5.36, null)
        ));
        assertEquals(players, new HashSet<>(topG.getPlayers().values()));

        PlayerInGame curP;
        for (PlayerInGame p : players) {
            curP = topG.getPlayer(p.getId());
            assertEquals(p.getPosition(), curP.getPosition());
            assertTrue(Math.abs(p.getBalance() - curP.getBalance()) < 0.01);
        }

        assertNotNull(topG.getWinners().get("480564b2"));
        assertTrue(Math.abs(topG.getWinners().get("480564b2") - 1.41) < 0.005);
        assertEquals(1, topG.getWinners().size());

        assertEquals(0, topG.getExtraCashAmount());
        assertEquals(new Hand("Kd", "Ks"), topG.getPlayer("480564b2").getHand());
        assertEquals(new Hand("Td", "Ts"), topG.getPlayer("Hero").getHand());

        ArrayList<Action> actions = new ArrayList<>(List.of(
                new Action(BLIND, "Hero", 0.02, 0),
                new Action(BLIND, "480564b2", 0.05, 0.02),
                new Action(FOLD, "59435a5e", 0, 0.07),
                new Action(FOLD, "dd1dce69", 0.15, 0.07),
                new Action(FOLD, "363e9d13", 0, 0.07),
                new Action(FOLD, "1e9152a8", 0, 0.07),
                new Action(RAISE, "Hero", 0.15, 0.07),
                new Action(RAISE, "480564b2", 0.44, 0.2),
                new Action(CALL, "Hero", 0.29, 0.59)
        ));
        assertEquals(actions, topG.getPreFlop().getAllActions());


        StreetDescription flop = new StreetDescription(1.48,
                new Board("5h", "7d", "Jh"),
                new HashSet<PlayerInGame>(List.of(
                        new PlayerInGame("Hero", SB, 8.02),
                        new PlayerInGame("480564b2", BB, 5.23))),
                new ArrayList<Action>(List.of(
                        new Action(CHECK, "Hero", 0.0, 0.88),
                        new Action(BET, "480564b2", 0.3, 0.88),
                        new Action(CALL, "Hero", 0.3, 1.18)
                )));

        assertEquals(flop, topG.getFlop());

        StreetDescription turn = new StreetDescription(
                1.48,
                new Board("5h", "7d", "Jh", "4c"),
                new HashSet<PlayerInGame>(List.of(
                        new PlayerInGame("480564b2", BB, 4.49))),
                new ArrayList<Action>(List.of(
                        new Action(CHECK, "Hero", 0.0, 1.48),
                        new Action(BET, "480564b2", 1.11, 1.48),
                        new Action(FOLD, "Hero", 0, 2.59)
                ))
        );
        assertEquals(turn, topG.getTurn());

        assertEquals(new Hand("Kd", "Ks"), topG.getPlayer("480564b2").getHand());

        assertNull(topG.getRiver());

        assertFalse(topG.getFlop().isAllIn());
        assertFalse(topG.getTurn().isAllIn());
    }

    @Test
    public void testEarlyAllInCashoutGameSplitting()
            throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String text = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/earlyAllInCashoutGame.txt");

        Game topG = parser.parseGame(text);
        String strRep = """
                (Game| Game Id: RC1281883052,
                Players: [(PlayerInGame| UserName: _UNDEFINED_, Id: Hero, Pos: SB, Balance: 13.40), (PlayerInGame| UserName: _UNDEFINED_, Id: 16d75d78, Pos: BB, Balance: 6.28), (PlayerInGame| UserName: _UNDEFINED_, Id: c1f81489, Pos: LJ, Balance: 10.52), (PlayerInGame| UserName: _UNDEFINED_, Id: 361dc4bb, Pos: HJ, Balance: 2.31), (PlayerInGame| UserName: _UNDEFINED_, Id: 219f215e, Pos: CO, Balance: 7.88), (PlayerInGame| UserName: _UNDEFINED_, Id: c491325f, Pos: BTN, Balance: 6.12)],
                Preflop: (StreetDescription| Board: null, pot after betting: 0.33, Players after betting: [(PlayerInGame| UserName: _UNDEFINED_, Id: 219f215e, Pos: CO, Balance: 7.75), (PlayerInGame| UserName: _UNDEFINED_, Id: c491325f, Pos: BTN, Balance: 5.99)],
                 Actions: [(Action| Type: BLIND, Amount: 0.02, Pot before action: 0.00, Player Id: Hero), (Action| Type: BLIND, Amount: 0.05, Pot before action: 0.02, Player Id: 16d75d78), (Action| Type: FOLD, Pot before action: 0.07, Player Id: c1f81489), (Action| Type: FOLD, Pot before action: 0.07, Player Id: 361dc4bb), (Action| Type: RAISE, Amount: 0.13, Pot before action: 0.07, Player Id: 219f215e), (Action| Type: CALL, Amount: 0.13, Pot before action: 0.20, Player Id: c491325f), (Action| Type: FOLD, Pot before action: 0.33, Player Id: Hero), (Action| Type: FOLD, Pot before action: 0.33, Player Id: 16d75d78)],
                Flop: (StreetDescription| Board: ([5♠, 3♣, K♠]), pot after betting: 12.31, Players after betting: [(PlayerInGame| UserName: _UNDEFINED_, Id: 219f215e, Pos: CO, Balance: 1.76), (PlayerInGame| UserName: _UNDEFINED_, Id: c491325f, Pos: BTN, Balance: 0.00)],
                 Actions: [(Action| Type: BET, Amount: 0.20, Pot before action: 0.33, Player Id: 219f215e), (Action| Type: RAISE, Amount: 5.99, Pot before action: 0.53, Player Id: c491325f), (Action| Type: CALL, Amount: 5.79, Pot before action: 6.52, Player Id: 219f215e)],
                Turn: null,
                River: null)""";
        assertEquals(strRep, topG.toString());
        assertTrue(topG.getFlop().isAllIn());
        assertNull(topG.getTurn());
        assertNull(topG.getRiver());

        Hand heroHand = new Hand("3s", "9d");
        Hand h1 = new Hand("Ah", "Kh");
        Hand h2 = new Hand("5d", "5h");

        assertEquals(heroHand, topG.getPlayer("Hero").getHand());
        assertEquals(h2, topG.getPlayer("c491325f").getHand());
        assertEquals(h1, topG.getPlayer("219f215e").getHand());
    }

    @Test
    public void testPreFlopFoldedGameParsing() throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String text = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/preflopFoldedGame.txt");

        Game topG = parser.parseGame(text);

        String rep = """
                (Game| Game Id: RC1281882647,
                Players: [(PlayerInGame| UserName: _UNDEFINED_, Id: a5003d17, Pos: SB, Balance: 9.36), (PlayerInGame| UserName: _UNDEFINED_, Id: 6be2f5ab, Pos: BB, Balance: 5.00), (PlayerInGame| UserName: _UNDEFINED_, Id: Hero, Pos: LJ, Balance: 5.65), (PlayerInGame| UserName: _UNDEFINED_, Id: 9a4f339a, Pos: HJ, Balance: 17.79), (PlayerInGame| UserName: _UNDEFINED_, Id: 1ef61f80, Pos: CO, Balance: 15.01), (PlayerInGame| UserName: _UNDEFINED_, Id: cdd7fe2b, Pos: BTN, Balance: 10.00)],
                Preflop: (StreetDescription| Board: null, pot after betting: 0.12, Players after betting: [(PlayerInGame| UserName: _UNDEFINED_, Id: 9a4f339a, Pos: HJ, Balance: 17.74)],
                 Actions: [(Action| Type: BLIND, Amount: 0.02, Pot before action: 0.00, Player Id: a5003d17), (Action| Type: BLIND, Amount: 0.05, Pot before action: 0.02, Player Id: 6be2f5ab), (Action| Type: FOLD, Pot before action: 0.07, Player Id: Hero), (Action| Type: RAISE, Amount: 0.13, Pot before action: 0.07, Player Id: 9a4f339a), (Action| Type: FOLD, Pot before action: 0.20, Player Id: 1ef61f80), (Action| Type: FOLD, Pot before action: 0.20, Player Id: cdd7fe2b), (Action| Type: FOLD, Pot before action: 0.20, Player Id: a5003d17), (Action| Type: FOLD, Pot before action: 0.20, Player Id: 6be2f5ab)],
                Flop: null,
                Turn: null,
                River: null)""";
        assertEquals(rep, topG.toString());

        assertEquals(new Hand("Tc", "5s"), topG.getPlayer("Hero").getHand());
        assertFalse(topG.getPreFlop().isAllIn());
    }

    @Test
    public void testTwoRunoutsGame() throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String txt = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/allInTwoRunoutsGame.txt");
        Game topG = parser.parseGame(txt);

    }
    // endregion

    // region Normal Games Parsing

    @Test
    public void testFullGame() throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String text = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/fullGame.txt");

        Game topG = parser.parseGame(text);

        String s = """
                (Game| Game Id: RC1221774448,
                Players: [(PlayerInGame| UserName: _UNDEFINED_, Id: ea0c9d4e, Pos: SB, Balance: 51.37), (PlayerInGame| UserName: _UNDEFINED_, Id: 195539ef, Pos: BB, Balance: 27.37), (PlayerInGame| UserName: _UNDEFINED_, Id: 392d7ce5, Pos: LJ, Balance: 25.35), (PlayerInGame| UserName: _UNDEFINED_, Id: e41b54eb, Pos: HJ, Balance: 26.82), (PlayerInGame| UserName: _UNDEFINED_, Id: a7067c39, Pos: CO, Balance: 82.87), (PlayerInGame| UserName: _UNDEFINED_, Id: Hero, Pos: BTN, Balance: 39.13)],
                Preflop: (StreetDescription| Board: null, pot after betting: 4.24, Players after betting: [(PlayerInGame| UserName: _UNDEFINED_, Id: ea0c9d4e, Pos: SB, Balance: 50.79), (PlayerInGame| UserName: _UNDEFINED_, Id: 195539ef, Pos: BB, Balance: 26.79), (PlayerInGame| UserName: _UNDEFINED_, Id: a7067c39, Pos: CO, Balance: 82.29)],
                 Actions: [(Action| Type: BLIND, Amount: 0.10, Pot before action: 2.50, Player Id: ea0c9d4e), (Action| Type: BLIND, Amount: 0.25, Pot before action: 2.60, Player Id: 195539ef), (Action| Type: FOLD, Pot before action: 2.85, Player Id: 392d7ce5), (Action| Type: FOLD, Pot before action: 2.85, Player Id: e41b54eb), (Action| Type: RAISE, Amount: 0.58, Pot before action: 2.85, Player Id: a7067c39), (Action| Type: FOLD, Pot before action: 3.43, Player Id: Hero), (Action| Type: CALL, Amount: 0.48, Pot before action: 3.43, Player Id: ea0c9d4e), (Action| Type: CALL, Amount: 0.33, Pot before action: 3.91, Player Id: 195539ef)],
                Flop: (StreetDescription| Board: ([9♣, 3♠, 8♠]), pot after betting: 9.74, Players after betting: [(PlayerInGame| UserName: _UNDEFINED_, Id: 195539ef, Pos: BB, Balance: 24.04), (PlayerInGame| UserName: _UNDEFINED_, Id: a7067c39, Pos: CO, Balance: 79.54)],
                 Actions: [(Action| Type: CHECK, Pot before action: 4.24, Player Id: ea0c9d4e), (Action| Type: CHECK, Pot before action: 4.24, Player Id: 195539ef), (Action| Type: BET, Amount: 2.75, Pot before action: 4.24, Player Id: a7067c39), (Action| Type: FOLD, Pot before action: 6.99, Player Id: ea0c9d4e), (Action| Type: CALL, Amount: 2.75, Pot before action: 6.99, Player Id: 195539ef)],
                Turn: (StreetDescription| Board: ([9♣, 3♠, 8♠, K♦]), pot after betting: 9.74, Players after betting: [(PlayerInGame| UserName: _UNDEFINED_, Id: 195539ef, Pos: BB, Balance: 24.04), (PlayerInGame| UserName: _UNDEFINED_, Id: a7067c39, Pos: CO, Balance: 79.54)],
                 Actions: [(Action| Type: CHECK, Pot before action: 9.74, Player Id: 195539ef), (Action| Type: CHECK, Pot before action: 9.74, Player Id: a7067c39)],
                River: (StreetDescription| Board: ([9♣, 3♠, 8♠, K♦, 6♥]), pot after betting: 9.74, Players after betting: [(PlayerInGame| UserName: _UNDEFINED_, Id: 195539ef, Pos: BB, Balance: 24.04), (PlayerInGame| UserName: _UNDEFINED_, Id: a7067c39, Pos: CO, Balance: 79.54)],
                 Actions: [(Action| Type: CHECK, Pot before action: 9.74, Player Id: 195539ef), (Action| Type: CHECK, Pot before action: 9.74, Player Id: a7067c39)])""";
        assertEquals(topG.getPlayer("195539ef").getHand(), new Hand("8h", "Ah"));
        assertEquals(s, topG.toString());
        assertFalse(topG.getRiver().isAllIn());
    }

    @Test
    public void testProblemGame() throws FileNotFoundException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        String text = getTextFromFile("/ggPokerokFiles/gamesFiles/rushNCash/problemGame.txt");

        Game topG = parser.parseGame(text);
    }
    // endregion

    private String getFullPath(String path) {
        URL url = rushNCashParsingTest.class.getResource(path);
        assert url != null;

        return url.getFile().replace("%20", " ");
    }

    @Test
    public void testFileParsing() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = getFullPath("/ggPokerokFiles/gamesFiles/rushNCash/gameSession.txt");

        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        assertDoesNotThrow(() -> parser.parseFile(path));

        ArrayList<Game> games = parser.parseFile(path);
        assertEquals(166, games.size());
    }

    @Test
    public void testFileParsing2() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = getFullPath("/ggPokerokFiles/gamesFiles/rushNCash/gameSession2.txt");

        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        assertDoesNotThrow(() -> parser.parseFile(path));

        ArrayList<Game> games = parser.parseFile(path);
        assertEquals(375, games.size());
    }

    @Test
    public void testFilePArsing3() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = getFullPath("/ggPokerokFiles/gamesFiles/rushNCash/gameSession3.txt");

        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        assertDoesNotThrow(() -> parser.parseFile(path));

        ArrayList<Game> games = parser.parseFile(path);
        assertEquals(167, games.size());
    }

    @Test
    public void testDirectoryParsing() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = getFullPath("/ggPokerokFiles/gamesFiles/rushNCash/severalSessions");
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();

        ArrayList<Game> allGaes = parser.parseDirectoryFiles(path);

        // Verified Amount on pokerCraft
        assertEquals(3205, allGaes.size());

        Set<Game> gamesWShownCard = new HashSet<>();
        for (int i = 0; i < allGaes.size(); ++i) {
            if (allGaes.get(i).getShownOneCards().size() != 0) {
                gamesWShownCard.add(allGaes.get(i));
            }
        }
        assertEquals(9, gamesWShownCard.size());
    }

    @Test
    public void testBigDirectoryParsing() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = getFullPath("/ggPokerokFiles/gamesFiles/rushNCash/bigDirectory1");
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();

        ArrayList<Game> allGaes = parser.parseDirectoryFiles(path);

        // Verified Amount on pokerCraft
        assertEquals(16932, allGaes.size());

        Set<Game> gamesWShownCard = new HashSet<>();
        for (int i = 0; i < allGaes.size(); ++i) {
            if (allGaes.get(i).getShownOneCards().size() != 0) {
                gamesWShownCard.add(allGaes.get(i));
            }
        }
//        assertEquals(9, gamesWShownCard.size());
    }

    @Test
    public void testBalanceChanging() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/balanceGameTest.txt";
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        ArrayList<Game> games = parser.parseFile(new File(path).getAbsolutePath());

        double rake = 0.08;
        assertTrue(Math.abs(0.9 - rake - games.get(0).getHeroWinloss()) < 0.01);
    }

    @Test
    public void testFinalPotReturningUncalledBets() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/preFlopAllInExtraCashGame.txt";
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        Game game = parser.parseFile(path).get(0);
        assertEquals(13.61, game.getFinalPot());
    }

    @Test
    public void testCorrectBalance() throws IOException, IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
        Game game = parser.parseFile("src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/returningChipsToBalanceGame.txt").get(0);

        assertEquals("RC1328499375", game.getGameId());
        assertTrue(Math.abs(0.82 -  game.getHeroWinloss()) < 0.005);
        assertTrue(Math.abs(1.75 - game.getFinalPot()) < 0.005);
        assertTrue(GameAnalyzer.isPot3Bet(game));
    }

    // Corrupted text file (blinds are nor written in the file downloaded from the website).

//    @Test
//    public void parseProblemSession() throws IncorrectHandException, IncorrectBoardException, IOException, IncorrectCardException {
//        GGPokerokRushNCashParser parser = new GGPokerokRushNCashParser();
//        String path = "src/test/resources/ggPokerokFiles/gamesFiles/rushNCash/problemSession.txt";
//        assertDoesNotThrow(() -> parser.parseFile(path));
//    }

}
