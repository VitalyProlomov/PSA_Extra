package parsers.gg;

import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.lang.Double.parseDouble;
import static models.Action.ActionType.*;
import static models.PositionType.*;

public class GGPokerokHoldem9MaxParser implements GGParser {
    private int curLine = 0;

    /**
     * Parses text representation of the game.
     *
     * @param gameText text of the game (must be in correct format - as it is on Pokercraft.com)
     * @return instance of the game that was embedded into the given text
     * @throws IncorrectCardException
     * @throws IncorrectHandException
     * @throws IncorrectBoardException
     */
    @Override
    public Game parseGame(String gameText)
            throws IncorrectCardException, IncorrectHandException, IncorrectBoardException {
        curLine = 0;
        String[] lines = gameText.split("\n");

        ArrayList<ArrayList<String>> wordsInLines = new ArrayList<>();
        for (String line : lines) {
            wordsInLines.add(new ArrayList<>(List.of(line.split(" "))));
        }

        String handId = parseHandId(wordsInLines);
        double bbSize = parseBBSize(wordsInLines);

        Date date = parseDate(wordsInLines);
        String table = parseTable(wordsInLines);
        ArrayList<PlayerInGame> players = parsePlayers(wordsInLines);

        Game game = initiateGame(handId, bbSize, players, date, table);

        // Extra cash is not in the pot in 9Max, so I do not count it,
        // since the goal of the app is to analyze the play, not the rake/winloss affection
        // parseExtraCash(game, wordsInLines);

        parseHeroHand(game, wordsInLines);
        parseStreetDescriptions(game, wordsInLines, game.getExtraCashAmount());

        parseWinnings(game, wordsInLines);
        return game;
    }

    private double parseBBSize(ArrayList<ArrayList<String>> wordsInLines) {
        return parseDouble(wordsInLines.get(0).get(6).split("/[$]")[1].split("[)]")[0]);
    }

    private String parseHandId(ArrayList<ArrayList<String>> wordsInLines) {
        String handId = wordsInLines.get(curLine).get(2);
        handId = handId.substring(1, handId.length() - 1);

        return handId;
    }

    private Date parseDate(ArrayList<ArrayList<String>> wordsInLines) {
        String dateRep = wordsInLines.get(curLine).get(8);
        dateRep += " " + wordsInLines.get(curLine).get(9);

        ++curLine;
        return new Date(dateRep);
    }

    private String parseTable(ArrayList<ArrayList<String>> wordsInLines) {
        return wordsInLines.get(curLine).get(1);
    }

    private ArrayList<PlayerInGame> parsePlayers(ArrayList<ArrayList<String>> wordsInLines) {
        // Getting info about players abd setting to the game
        ArrayList<String> hashes = new ArrayList<>();
        ArrayList<Double> balances = new ArrayList<>();

        int btnSeatNumber = Integer.parseInt(wordsInLines.get(curLine).get(4).substring(1));

        ++curLine;

        int btnIndex = 0;
        while (wordsInLines.get(curLine).get(0).equals("Seat")){
            hashes.add(wordsInLines.get(curLine).get(2));
            String balanceStr = wordsInLines.get(curLine).get(3);
            balances.add(parseDouble(balanceStr.substring(2)));

            if (Integer.parseInt(wordsInLines.get(curLine).get(1).substring(0, 1)) == btnSeatNumber) {
                // The first player seat is located on 3rd line of the text.
                btnIndex = curLine - 2;
            }
            ++curLine;
        }

        ArrayList<PositionType> allPositions = new ArrayList<>(List.of(
                BTN, SB, BB,
                UTG, UTG_1,UTG_2,
                LJ, HJ, CO));

        ArrayList<PlayerInGame> players = new ArrayList<>();
        int size = hashes.size();

        ArrayList<PositionType> curPositionsUsed = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            curPositionsUsed.add(allPositions.get(i));
        }
        // Decided to make an exception (since in heads up variation (when only 2 players are present)
        // the small blind is posted by the player on the BTN position and the other player posts big blind.
        // So, I decided to change the positions names.
        if (size == 2) {
            curPositionsUsed.set(0, BTN);
            curPositionsUsed.set(1, BB);
        }
        for (int i = 0; i < hashes.size(); ++i) {
            players.add(new PlayerInGame(hashes.get((btnIndex + i) % size), curPositionsUsed.get(i), balances.get((btnIndex + i) % size)));
        }

        return players;
    }

    private Game initiateGame(String handId, double bbSize,
                              ArrayList<PlayerInGame> players,
                              Date date, String table) {
        HashMap<String, Double> initBalances = new HashMap<>();
        for (PlayerInGame p : players) {
            initBalances.put(p.getId(), p.getBalance());
        }

        HashMap<String, PlayerInGame> playersMap = new HashMap<>();
        for (PlayerInGame p : players) {
            playersMap.put(p.getId(), p);
        }

        Game game = new Game(handId, bbSize, playersMap, initBalances);
        game.setDate(date);
        game.setTable(table);

        game.setGameType(Game.GameType.HOLDEM_9MAX);

        return game;
    }

    private void parseExtraCash(Game game, ArrayList<ArrayList<String>> wordsInLines) {
        if (wordsInLines.get(curLine).get(0).equals("Cash")) {
            ArrayList<String> cashLine = wordsInLines.get(curLine);
            // In case I would want to collect stats about cash drops.
            double extraCash = parseDouble(wordsInLines.get(curLine).get(cashLine.size() - 1).substring(1));
            game.setExtraCash(extraCash);
        } else {
            game.setExtraCash(0);
        }
    }


    private void parseHeroHand(Game game, ArrayList<ArrayList<String>> wordsInLines)
            throws IncorrectCardException, IncorrectHandException {
        while (!wordsInLines.get(curLine).get(2).equals("Hero")) {
            ++curLine;
        }

        Card c1 = new Card(wordsInLines.get(curLine).get(3).substring(1));
        Card c2 = new Card((wordsInLines.get(curLine).get(4).substring(0, 2)));
        Hand heroHand = new Hand(c1, c2);
        game.setHeroHand(heroHand);
    }

    private void parseStreetDescriptions(Game game, ArrayList<ArrayList<String>> wordsInLines, double extraCashAmount)
            throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        parsePreFlop(game, wordsInLines, extraCashAmount);

        parseFlop(game, wordsInLines);
        parseTurn(game, wordsInLines);
        parseRiver(game, wordsInLines);
//        double pot = game.
//        parseFlop(game, wordsInLines, pot);
    }

    private void parseMissedBlindsAndAntes(ArrayList<ArrayList<String>> wordsInLines, Game game, StreetDescription st) {
        ArrayList<Action> actionArray = new ArrayList<>();
        double curPot = 0;
        // Antes
        while (wordsInLines.get(curLine).size() == 5 && wordsInLines.get(curLine).get(3).equals("ante")) {
            // cutting ':' at the end.
            String hash = wordsInLines.get(curLine).get(0).substring(0, wordsInLines.get(curLine).get(0).length() - 1);
            double anteAmount = Double.parseDouble(wordsInLines.get(curLine).get(4).substring(1));
            Action action = new Action(ANTE, hash, anteAmount, curPot);

            st.addActionAndUpdateBalances(action, anteAmount);
            game.decrementPlayersBalance(hash, anteAmount);

            curPot += anteAmount;
            ++curLine;
        }

        // Small Blind
        String hash = wordsInLines.get(curLine).get(0).substring(0, wordsInLines.get(curLine).get(0).length() - 1);
        double amount = Double.parseDouble(wordsInLines.get(curLine).get(4).substring(1));
        st.addActionAndUpdateBalances(new Action(Action.ActionType.BLIND, hash, amount, curPot), amount);
        curPot += amount;
        game.decrementPlayersBalance(hash, amount);
        ++curLine;

        // Big Blind
        hash = wordsInLines.get(curLine).get(0).substring(0, wordsInLines.get(curLine).get(0).length() - 1);
        amount = Double.parseDouble(wordsInLines.get(curLine).get(4).substring(1));
        st.addActionAndUpdateBalances(new Action(Action.ActionType.BLIND,hash, amount, curPot), amount);
        curPot += amount;
        game.decrementPlayersBalance(hash, amount);
        ++curLine;

        while (!wordsInLines.get(curLine).get(0).equals("***")) {
            Action action;
            amount = 0;
            if (wordsInLines.get(curLine).get(1).equals("straddle")) {
                hash = wordsInLines.get(curLine).get(0).substring(0, wordsInLines.get(curLine).get(0).length() - 1);
                amount = Double.parseDouble(wordsInLines.get(curLine).get(2).substring(1));
                action = new Action(STRADDLE, hash, amount, curPot);

                // Uses the fact that antes are placed before straddle
                int ind = st.getAllActions().size() - 1;
                while (!st.getAllActions().get(ind).getPlayerId().equals(hash) && ind >= 0) {
                    --ind;
                }
                if (!st.getAllActions().get(ind).getActionType().equals(ANTE)) {
                    amount -= st.getAllActions().get(ind).getAmount();
                }

                // If chips are going straight into the pot, then MISSED_BLIND is assigned
                // as ActionType, but if chips are going into players bet, then BLIND type is assigned.
            } else if (wordsInLines.get(curLine).get(2).equals("missed")) {
                hash = wordsInLines.get(curLine).get(0).substring(0, wordsInLines.get(curLine).get(0).length() - 1);
                amount = Double.parseDouble(wordsInLines.get(curLine).get(4).substring(1));
                action = new Action(MISSED_BLIND, hash, amount, curPot);
            } else if (wordsInLines.get(curLine).get(2).equals("big")) {
                hash = wordsInLines.get(curLine).get(0).substring(0, wordsInLines.get(curLine).get(0).length() - 1);
                amount = Double.parseDouble(wordsInLines.get(curLine).get(4).substring(1));
                action = new Action(BLIND, hash, amount, curPot);
            } else {
                throw new RuntimeException("Unexpected preflop action, make sure file is not corrupted.");
            }
            st.addActionAndUpdateBalances(action, amount);
            game.decrementPlayersBalance(hash, amount);

            curPot += amount;
            ++curLine;
        }

        // Now the line must be "*** HOLE CARDS ***"
        curLine += 1;

        st.setPotAfterBetting(curPot);
        while (wordsInLines.get(curLine).get(0).equals("Dealt")) {
            ++curLine;
        }
    }

    private void parsePreFlop(Game game, ArrayList<ArrayList<String>> wordsInLines, double extraCashAmount)
            throws IncorrectHandException, IncorrectCardException {
        while (!wordsInLines.get(curLine).get(0).equals("Seat")) {
            --curLine;
        }
        while (!wordsInLines.get(curLine).get(1).equals("posts")) {
            ++curLine;
        }

        StreetDescription pfsd = parseStreetAction(game, wordsInLines, 0);
        game.setPreFlop(pfsd);
    }

    private void parseFlop(Game game, ArrayList<ArrayList<String>> wordsInLines)
            throws IncorrectCardException, IncorrectBoardException, IncorrectHandException {
        if (game.getPreFlop().getPlayersAfterBetting().size() == 1 || game.getPreFlop().isAllIn()) {
            return;
        }
        while (wordsInLines.get(curLine).size() < 2 || !wordsInLines.get(curLine).get(1).equals("FLOP")) {
            ++curLine;
        }

        Card c1 = new Card(wordsInLines.get(curLine).get(3).substring(1));
        Card c2 = new Card(wordsInLines.get(curLine).get(4));
        Card c3 = new Card(wordsInLines.get(curLine).get(5).substring(0, 2));
        Board flopBoard = new Board(c1, c2, c3);

        ++curLine;
        StreetDescription flop;
        if (!game.getPreFlop().isAllIn()) {
            flop = parseStreetAction(game, wordsInLines, game.getPreFlop().getPotAfterBetting());
        } else {
            flop = new StreetDescription(
                    game.getPreFlop().getPotAfterBetting(),
                    flopBoard,
                    game.getPreFlop().getPlayersAfterBetting(),
                    new ArrayList<>());
            flop.setAllIn(true);
        }

        flop.setBoard(flopBoard);
        game.setFlop(flop);

        while (!wordsInLines.get(curLine).get(0).equals("***")) {
            ++curLine;
        }
    }

    private void parseTurn(Game game, ArrayList<ArrayList<String>> wordsInLines) throws IncorrectCardException, IncorrectBoardException, IncorrectHandException {
        if (game.getFlop() == null || game.getFlop().getPlayersAfterBetting().size() == 1 || game.getFlop().isAllIn()) {
            return;
        }
        while (wordsInLines.get(curLine).size() < 2 || !wordsInLines.get(curLine).get(1).equals("TURN")) {
            ++curLine;
        }

        Card tCard = new Card(wordsInLines.get(curLine).get(6).substring(1, 3));
        ++curLine;

        double curPot = game.getFlop().getPotAfterBetting();

        // Cards will be added later in this method
        StreetDescription turn;
        if (!game.getFlop().isAllIn()) {
            turn = parseStreetAction(game, wordsInLines, curPot);
        } else {
            turn = new StreetDescription(
                    game.getFlop().getPotAfterBetting(),
                    null,
                    game.getFlop().getPlayersAfterBetting(),
                    new ArrayList<>());
            turn.setAllIn(true);
        }

        ArrayList<Card> cards = new ArrayList<>(game.getFlop().getBoard().getCards());
        cards.add(tCard);
        turn.setBoard(new Board(cards));

        game.setTurn(turn);
    }

    private void parseRiver(Game game, ArrayList<ArrayList<String>> wordsInLines)
            throws IncorrectCardException, IncorrectBoardException, IncorrectHandException {
        if (game.getTurn() == null || game.getTurn().getPlayersAfterBetting().size() == 1 || game.getTurn().isAllIn()) {
            return;
        }
        while (wordsInLines.get(curLine).size() < 2 || !wordsInLines.get(curLine).get(1).equals("RIVER")) {
            ++curLine;
        }
        Card rCard = new Card(wordsInLines.get(curLine).get(7).substring(1, 3));
        ++curLine;

        double curPot = game.getTurn().getPotAfterBetting();
        StreetDescription river;

        if (!game.getTurn().isAllIn()) {
            river = parseStreetAction(game, wordsInLines, curPot);
            // if (game.getRiver().getPlayersAfterBetting().size() > 1) {
            parseAndAddShownHands(game, wordsInLines);
            // }
        } else {
            river = new StreetDescription(
                    game.getTurn().getPotAfterBetting(),
                    null,
                    game.getTurn().getPlayersAfterBetting(),
                    new ArrayList<>());
            river.setAllIn(true);
        }

        ArrayList<Card> cards = new ArrayList<>(game.getTurn().getBoard().getCards());
        cards.add(rCard);
        river.setBoard(new Board(cards));

        game.setRiver(river);
    }

    private StreetDescription parseStreetAction(Game game, ArrayList<ArrayList<String>> wordsInLines, double curPot) throws IncorrectCardException, IncorrectHandException {
        StreetDescription st = new StreetDescription();
        // Adding blinds posting and antes and players left on pre-flop.
        if (Math.abs(curPot) < 0.001) {
            parseMissedBlindsAndAntes(wordsInLines, game, st);
            st.setPlayersAfterBetting(game.getPlayers().values());
            curPot = st.getPotAfterBetting();
        }
        // Setting players is essential for the condition in the next while loop.
        else {
            if (game.getTurn() != null) {
                st.setPlayersAfterBetting(game.getTurn().getPlayersAfterBetting());
            } else if (game.getFlop() != null) {
                st.setPlayersAfterBetting(game.getFlop().getPlayersAfterBetting());
            } else if (game.getPreFlop() != null) {
                st.setPlayersAfterBetting(game.getPreFlop().getPlayersAfterBetting());
            }
        }
        st.setPotAfterBetting(curPot);

        while (wordsInLines.get(curLine).get(0).charAt(wordsInLines.get(curLine).get(0).length() - 1) == ':' &&
                st.getPlayersAfterBetting().size() > 1 &&
                !wordsInLines.get(curLine).get(1).equals("shows") &&
                !wordsInLines.get(curLine).get(1).equals("Pays")) {
            String hash = wordsInLines.get(curLine).get(0);
            hash = hash.substring(0, hash.length() - 1);
            PlayerInGame curPlayer = game.getPlayer(hash);

            // For me
            if (curPlayer == null) {
                throw new RuntimeException("Code is incorrect - couldn't find the player " +
                        "with given hash in array of players in game.");
            }
            ArrayList<String> lineW = wordsInLines.get(curLine);
            addAction(lineW, game, st, curPlayer);

//            if (lineW.get(lineW.size() - 1).equals("all-in")) {
//                game.setPlayerStatus(curPlayer.getId(), PlayerStatus.ALL_IN);
//            }
            ++curLine;
        }

        while (wordsInLines.get(curLine).get(0).equals("Uncalled")) {
            String id = wordsInLines.get(curLine).get(5);
            String amountStr = wordsInLines.get(curLine).get(2).substring(2);
            amountStr = amountStr.substring(0, amountStr.length() - 1);
            double returnedAmount = Double.parseDouble(amountStr);
            game.returnUncalledChips(id, returnedAmount);
            st.returnUncalledChips(id, returnedAmount);
            ++curLine;
        }

        // Getting shown hands if all players are all-in.
        while (wordsInLines.get(curLine).get(1).equals("shows")) {
            String id = wordsInLines.get(curLine).get(0);
            id = id.substring(0, id.length() - 1);
            Card card1 = new Card(wordsInLines.get(curLine).get(2).substring(1, 3));
            if (wordsInLines.get(curLine).size() >= 4 &&
                    wordsInLines.get(curLine).get(3).length() >= 3 &&
                    wordsInLines.get(curLine).get(3).charAt(2) == ']') {
                Card card2 = new Card(wordsInLines.get(curLine).get(3).substring(0, 2));
                game.setPlayerHand(id, new Hand(card1, card2));
            } else {
                game.addShownOneCard(id, card1);
            }
            ++curLine;
        }

        // Counts amount of all-in players - needed for correct all-in street
        // assessment if a player went all-in on previous street, but players continued
        // playing on later streets.
        int allInAm = 0;
        for (PlayerInGame p : st.getPlayersAfterBetting()) {
            if (p.getBalance() < 0.01) {
                ++allInAm;
            }
        }
        if (st.getPlayersAfterBetting().size() > 1 && allInAm >= st.getPlayersAfterBetting().size() - 1) {
            st.setAllIn(true);
        }

        return st;
    }

    private void addAction(ArrayList<String> line, Game game, StreetDescription st, PlayerInGame curPlayer) {
        Action action;
        double amount = 0;
        st.addPlayerAfterBetting(curPlayer);
        switch (line.get(1)) {
            case "folds" -> {
                action = new Action(Action.ActionType.FOLD, curPlayer.getId(), 0, st.getPotAfterBetting());
                st.removePlayerAfterBetting(curPlayer);
            }
            case "raises" -> {
                double lastAmount = 0;
                for (int i = st.getAllActions().size() - 1; i >= 0; --i) {
                    if (st.getAllActions().get(i).getPlayerId().equals(curPlayer.getId())) {
                        // If he folded, he wouldnt be raising now, so old case is impossible.
                        // If he checked, it meant no one bet before him (or it is BB on pre-flop)
                        if (!st.getAllActions().get(i).getActionType().equals(ANTE) &&
                                !st.getAllActions().get(i).getActionType().equals(MISSED_BLIND)) {
                            lastAmount = st.getAllActions().get(i).getAmount();
                        }
                        break;
                    }
                }
                amount = parseDouble(line.get(4).substring(1));
                action = new Action(Action.ActionType.RAISE, curPlayer.getId(), amount, st.getPotAfterBetting());

                amount = amount - lastAmount;
                st.setPotAfterBetting(st.getPotAfterBetting() + amount);

                game.decrementPlayersBalance(curPlayer.getId(), amount);
            }
            case "calls" -> {
                amount = parseDouble(line.get(2).substring(1));
                action = new Action(Action.ActionType.CALL, curPlayer.getId(), amount, st.getPotAfterBetting());
                st.setPotAfterBetting(st.getPotAfterBetting() + amount);
                game.decrementPlayersBalance(curPlayer.getId(), amount);
            }
            case "bets" -> {
                amount = parseDouble(line.get(2).substring(1));
                action = new Action(Action.ActionType.BET, curPlayer.getId(), amount, st.getPotAfterBetting());
                st.setPotAfterBetting(st.getPotAfterBetting() + amount);
                game.decrementPlayersBalance(curPlayer.getId(), amount);
            }
            case "checks" -> action = new Action(Action.ActionType.CHECK, curPlayer.getId(), 0, st.getPotAfterBetting());
            default -> throw new RuntimeException("unexpected line in parsed file (was expected line with action, but got): " + line);
        }


//        if (game.getPlayer(curPlayer.getId()).getBalance() < 0.01) {
//            game.setPlayerStatus(curPlayer.getId(), PlayerStatus.ALL_IN);
//        }

        // st.setPlayerBalance(curPlayer.getId(), game.getPlayer(curPlayer.getId()).getBalance());
        st.addActionAndUpdateBalances(action, amount);
    }

    private void parseAndAddShownHands(Game game, ArrayList<ArrayList<String>> wordsInLines) throws IncorrectCardException, IncorrectHandException {
        while (!wordsInLines.get(curLine).get(0).equals("***")) {
            if (wordsInLines.get(curLine).get(1).equals("shows")) {
                String hash = (wordsInLines.get(curLine).get(0));
                hash = hash.substring(0, hash.length() - 1);

                Card c1 = new Card(wordsInLines.get(curLine).get(2).substring(1));

                Card c2 = new Card(wordsInLines.get(curLine).get(3).substring(0, 2));
                Hand hand = new Hand(c1, c2);

                game.setPlayerHand(hash, hand);
            }
            // Should see what other lines could be here (excluding showing of hands).
            ++curLine;
        }
    }

    private void parseWinnings(Game game, ArrayList<ArrayList<String>> wordsInLines) {
        int ax = 0;
        while (!wordsInLines.get(curLine).get(1).equals("SHOWDOWN") && !wordsInLines.get(curLine).get(1).equals("FIRST")) {
            ++curLine;
            ++ax;
            if (ax > 3) {
                ax = 0;
            }
        }
        ++curLine;

        while (!wordsInLines.get(curLine).get(1).equals("SUMMARY")) {
            if (wordsInLines.get(curLine).get(1).equals("collected")) {
                String id = wordsInLines.get(curLine).get(0);
                double amount = Double.parseDouble(wordsInLines.get(curLine).get(2).substring(1));
                game.addWinner(id, amount);
            }
            ++curLine;
        }

        ++curLine;
        String rakeStr = wordsInLines.get(curLine).get(5).substring(1);
        String jackpotRake = wordsInLines.get(curLine).get(8).substring(1);

        double amount = Double.parseDouble(rakeStr) + Double.parseDouble(jackpotRake);

        game.setRake(amount);
    }
}
