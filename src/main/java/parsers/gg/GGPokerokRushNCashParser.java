package parsers.gg;

import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.*;

import java.util.*;

import static java.lang.Double.parseDouble;
import static models.PositionType.BB;
import static models.PositionType.SB;

public class GGPokerokRushNCashParser implements GGParser {
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

        parseExtraCash(game, wordsInLines);
        parseHeroHand(game, wordsInLines);
        parseStreetDescriptions(game, wordsInLines, game.getExtraCashAmount());


        parseWinnings(game, wordsInLines);
        return game;
    }

    private double parseBBSize(ArrayList<ArrayList<String>> wordsInLines) {
        return parseDouble(wordsInLines.get(0).get(7).split("/[$]")[1].split("[)]")[0]);
    }

    private String parseHandId(ArrayList<ArrayList<String>> wordsInLines) {
        String handId = wordsInLines.get(curLine).get(2);
        handId = handId.substring(1, handId.length() - 1);

        return handId;
    }

    private Date parseDate(ArrayList<ArrayList<String>> wordsInLines) {
        String dateRep = wordsInLines.get(curLine).get(9);
        dateRep += " " + wordsInLines.get(curLine).get(10);

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

        ++curLine;
        for (int i = 0; i < 6; ++i) {
            hashes.add(wordsInLines.get(curLine + i).get(2));
            String balanceStr = wordsInLines.get(curLine + i).get(3);
            balances.add(parseDouble(balanceStr.substring(2)));
        }

        curLine += 6;

        ArrayList<PositionType> positions = new ArrayList<>(List.of(PositionType.BTN,
                SB, BB, PositionType.LJ, PositionType.HJ, PositionType.CO));

        ArrayList<PlayerInGame> players = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            players.add(new PlayerInGame(hashes.get(i), positions.get(i), balances.get(i)));
        }

        return players;
    }

    private Game initiateGame(String handId, double bbSize,
                              ArrayList<PlayerInGame> players,
                              Date date, String table) {
        HashMap<String, Double> initBalances  = new HashMap<String, Double>();
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

    private void parsePreFlop(Game game, ArrayList<ArrayList<String>> wordsInLines, double extraCashAmount)
            throws IncorrectHandException, IncorrectCardException {
        while (wordsInLines.get(curLine).get(0).equals("Dealt")) {
            ++curLine;
        }

        double initPot = game.getBigBlindSize$() + game.getSB() + extraCashAmount;
        StreetDescription pfsd = parseStreetAction(game, wordsInLines, initPot);
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
                    new ArrayList<Action>());
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
                    new ArrayList<Action>());
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
        // Adding blinds posting and players left on pre-flop.
        if (curPot - (game.getBigBlindSize$() + game.getSB() + game.getExtraCashAmount()) < 0.01) {
            st.addActionAndUpdateBalances(new Action(Action.ActionType.BLIND, game.getPosPlayersMap().get(SB).getId(), game.getSB(), game.getExtraCashAmount()), game.getSB());
            game.decrementPlayersBalance(game.getPosPlayersMap().get(SB).getId(), game.getSB());
            st.addActionAndUpdateBalances(new Action(Action.ActionType.BLIND, game.getPosPlayersMap().get(BB).getId(), game.getBigBlindSize$(), game.getSB() + game.getExtraCashAmount()), game.getBigBlindSize$());
            game.decrementPlayersBalance(game.getPosPlayersMap().get(BB).getId(), game.getBigBlindSize$());

            st.setPlayersAfterBetting(game.getPlayers().values());
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
                        lastAmount = st.getAllActions().get(i).getAmount();
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