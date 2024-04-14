//package parsers.gg;
//
//
//import exceptions.IncorrectBoardException;
//import exceptions.IncorrectCardException;
//import exceptions.IncorrectHandException;
//import models.*;
//
//import java.util.*;
//
//import static java.lang.Double.parseDouble;
//import static models.Action.ActionType.*;
//import static models.PositionType.*;
//
//public class GGPokerokHoldem8Max3BlindsParser implements GGParser {
//    private int curLine = 0;
//
//    HashMap<String, Double> balances = new HashMap<>();
////
////    HashMap<String, PlayerStatus> statuses;
//
//    @Override
//    public Game parseGame(String gameText)
//            throws IncorrectCardException, IncorrectHandException, IncorrectBoardException {
////        statuses = new HashMap<>();
//        curLine = 0;
//        String[] lines = gameText.split("\n");
//
//        ArrayList<ArrayList<String>> wordsInLines = new ArrayList<>();
//        for (String line : lines) {
//            wordsInLines.add(new ArrayList<>(List.of(line.split(" "))));
//        }
//        Game game = initiateGame(wordsInLines);
//        parseDate(game, wordsInLines);
//
//        int btnSeat = Integer.parseInt(wordsInLines.get(1).get(4).substring(1));
//        ++curLine;
//        parsePlayers(game, wordsInLines, btnSeat);
//
//        // parseExtraCash(game, wordsInLines);
//        ArrayList<Action> blindsAction = parseAntesAndBlinds(game, wordsInLines);
//        parseHeroHand(game, wordsInLines);
//
//        parseStreetDescriptions(game, wordsInLines, game.getExtraCashAmount());
//
//
//
//        return game;
//    }
//
//    private void parseStreetDescriptions(Game game, ArrayList<ArrayList<String>> wordsInLines, double extraCashAmount) {
////        parsePreFlop(game, wordsInLines, extraCashAmount);
////
//////        if (game.getPreFlop().isAllIn()) {
//////            parseAndAddShownHands();
//////        }
////        parseFlop(game, wordsInLines);
////        parseTurn(game, wordsInLines);
////        parseRiver(game, wordsInLines);
//    }
//
//    private Game initiateGame(ArrayList<ArrayList<String>> wordsInLines) {
//        String handId = wordsInLines.get(curLine).get(2);
//        handId = handId.substring(1, handId.length() - 1);
//
//        double bbSize = parseDouble(wordsInLines.get(0).get(7).split("/[$]")[1]);
//
//        // Creating a game with BB size (in dollars) and hand id.
//        return new Game(handId, bbSize);
//    }
//
//    private void parseDate(Game game, ArrayList<ArrayList<String>> wordsInLines) {
//        String dateRep = wordsInLines.get(curLine).get(9);
//        dateRep += " " + wordsInLines.get(curLine).get(10);
//        Date date = new Date(dateRep);
//        game.setDate(date);
//
//        ++curLine;
//    }
//
//
//    private void parsePlayers(Game game, ArrayList<ArrayList<String>> wordsInLines, int btnSeat) {
//        // Getting info about players abd setting to the game
//        ArrayList<String> hashes = new ArrayList<>();
////        ArrayList<Double> balances = new ArrayList<>();
//
//        while (wordsInLines.get(curLine).get(0).equals("Seat")) {
//            hashes.add(wordsInLines.get(curLine).get(2));
//            // Notice - increasing curLine here.
//            String balanceStr = wordsInLines.get(curLine).get(3);
//            balances.put(wordsInLines.get(curLine).get(2), parseDouble(balanceStr.substring(2)));
//            ++curLine;
//        }
//
//        ArrayList<PositionType> positions = createPlayersPositionsList(hashes.size());
//
//        ArrayList<PlayerInGame> players = new ArrayList<>();
//        for (int i = 0; i < hashes.size(); ++i) {
//            players.add(new PlayerInGame(hashes.get((i + btnSeat - 1) % hashes.size()),
//                    positions.get(i),
//                    balances.get(hashes.get((i + btnSeat - 1) % hashes.size())))
//            );
//        }
//
//        game.setPlayers(new HashSet<>(players));
//    }
//
//    private ArrayList<Action> parseAntesAndBlinds(Game game, ArrayList<ArrayList<String>> wordsInLines) {
//        ArrayList<Action> actions = new ArrayList<>();
//        double balance = 0.0;
//        // Ante and Blinds (SB, BB, TB and missed blinds).
//        int blindsCounter = 0;
//        while (wordsInLines.get(curLine).get(1).equals("posts")) {
//            String curPlayerId = wordsInLines.get(curLine).get(0);
//            curPlayerId = curPlayerId.substring(0, curPlayerId.length() - 1);
//            double amount = Double.parseDouble(wordsInLines.get(curLine).get(4).substring(1));
//
//            if (wordsInLines.get(curLine).get(3).equals("ante")) {
//                actions.add(new Action(ANTE, curPlayerId, amount, balance));
//            } else {
//                if (blindsCounter < 3) {
//                    actions.add(new Action(BLIND, curPlayerId, amount, balance));
//                } else {
//                    actions.add(new Action(MISSED_BLIND, curPlayerId, amount, balance));
//                }
//                ++blindsCounter;
//            }
//            balance+= amount;
//            ++curLine;
//
//            balances.replace(curPlayerId, balances.get(curPlayerId) - amount);
//        }
//
//        // Straddles
//        while (wordsInLines.get(curLine).get(1).equals("straddle")) {
//            String curPlayerId = wordsInLines.get(curLine).get(0);
//            curPlayerId = curPlayerId.substring(0, curPlayerId.length() - 1);
//            double amount = Double.parseDouble(wordsInLines.get(curLine).get(2).substring(1));
//
//            actions.add(new Action(STRADDLE, curPlayerId, amount, balance));
//            ++curLine;
//            balances.replace(curPlayerId, balances.get(curPlayerId) - amount);
//        }
//
//        // Simply for easier debugging.
//        if (!wordsInLines.get(curLine).get(0).equals("***")) {
//            throw new RuntimeException("Something came up after Straddle (unpredicted action) - fix it");
//        }
//
//        return actions;
//    }
//
//    public ArrayList<PositionType> createPlayersPositionsList(int playersAmount) {
//        ArrayList<PositionType> positions = new ArrayList<PositionType>();
//        positions.add(SB);
//        positions.add(BB);
//        if (playersAmount >= 3) {
//            positions.add(0, BTN);
//        }
//        if (playersAmount >= 4) {
//            positions.add(TB);
//        }
//        if (playersAmount >= 5) {
//            positions.add(CO);
//        }
//        if (playersAmount >= 6) {
//            positions.add(4, HJ);
//        }
//        if (playersAmount >= 7) {
//            positions.add(4, UTG);
//        }
//        if (playersAmount == 8) {
//            positions.add(5, UTG_1);
//        }
//        if (playersAmount > 8) {
//            throw new RuntimeException("players amount in game of 8-max 3 Blinds holdem is > 8." +
//                    " There are " + playersAmount + "players. FIX IT");
//        }
//
//        return positions;
//    }
//
//    private void parseHeroHand(Game game, ArrayList<ArrayList<String>> wordsInLines) throws IncorrectCardException, IncorrectHandException {
//        while (!wordsInLines.get(curLine).get(2).equals("Hero")) {
//            ++curLine;
//        }
//
//        Card c1 = new Card(wordsInLines.get(curLine).get(3).substring(1));
//        Card c2 = new Card((wordsInLines.get(curLine).get(4).substring(0, 2)));
//        Hand heroHand = new Hand(c1, c2);
//        game.setHeroHand(heroHand);
//    }
//}
