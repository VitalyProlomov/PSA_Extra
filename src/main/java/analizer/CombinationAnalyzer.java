package analizer;

import exceptions.IncorrectBoardException;
import models.Board;
import models.Card;
import models.ComboCardsPair;
import models.Hand;

import java.util.*;

import static analizer.Combination.FLUSH;
import static analizer.Combination.FLUSH_ROYAL;

/**
 * Class that contains methods for analyzing the board and identifying the
 * combination that are present on the board.
 */
public class CombinationAnalyzer {
    /**
     * Checks if the given set of cards is valid and could exist in real life.
     *
     * @param board Cards on the board
     * @param hand  hand of the player in the game
     * @return true if the board is valid, false otherwise.
     */
    public static boolean isBoardValid(Board board, Hand hand) throws IncorrectBoardException {
        ArrayList<Card> ext = board.getCards();
        if (hand != null) {
            ext.addAll(hand.getCards());
        }
        return isBoardValid(ext);
    }

    /**
     * Checks if the given set of cards is valid and could exist in real life.
     *
     * @param board     Cards on the board
     * @param deadCards all the cards not on the board that were either dealt to players or exposed or burned.
     * @return true if the board is valid, false otherwise.
     */
    public static boolean isBoardValid(Board board, ArrayList<Card> deadCards) {
        ArrayList<Card> ext = board.getCards();
        ext.addAll(deadCards);
        return isBoardValid(ext);
    }

    /**
     * Checks if the given set of cards is valid and could exist in real life.
     *
     * @param extendedBoard all the cards that are being checked
     * @return true if the board is valid, false otherwise.
     */
    public static boolean isBoardValid(ArrayList<Card> extendedBoard) {
        HashSet<Card> set = new HashSet<>(extendedBoard);
        return extendedBoard.size() == set.size();
    }

    /**
     * Sort Cards on board by rank. Keeps the order of same cards.
     * Uses min sort
     *
     * @param cards arrayList of cards that need to be sorted (could be any amount).
     */
    public static void sortBoard(ArrayList<Card> cards) {
        int min;
        int ind_min = 0;
        boolean isSwapNeeded;
        for (int i = 0; i < cards.size(); ++i) {
            isSwapNeeded = false;
            min = cards.get(i).getRank().value;
            for (int j = i; j < cards.size(); ++j) {
                if (min > cards.get(j).getRank().value) {
                    ind_min = j;
                    min = cards.get(j).getRank().value;
                    isSwapNeeded = true;
                }
            }

            if (isSwapNeeded) {
                Card tmp = cards.get(i);
                cards.set(i, cards.get(ind_min));
                cards.set(ind_min, tmp);
            }
        }
    }


    /**
     * Finds the best combination possible, using the community cards on the board and the hand given
     * Amount of cards of the board and the hand given must sum up to at least 5.
     * If several best combinations are available, then random one is returned (not really
     * random, but it depends on internal sorting algorithm), but it is guaranteed that
     * the best combination is going to be returned
     *
     * @param board community cards
     * @param hand  hand of the player, that will be used to make combination.
     * @return a pair of combination and the board that recreates this combination (the exact 5 cards)
     * @throws IncorrectBoardException  if the board is illegal (ex: doubling cards)
     * @throws IllegalArgumentException if amount of cards of the board and the hand combined is less than 5.
     */
    public static ComboCardsPair recognizeCombinationOnBoard(Board board, Hand hand)
            throws IncorrectBoardException {
        if (!isBoardValid(board, hand)) {
            throw new IncorrectBoardException();
        }

        ArrayList<Card> extendedCards = new ArrayList<Card>(board.getCards());
        if (hand != null) {
            extendedCards.addAll(hand.getCards());
        }

        if (extendedCards.size() < 5) {
            throw new IllegalArgumentException("Combination must consist of 5 cards, so at least 5 cards must be given");
        }

        return recognizeCombinationOnBoardChecked(board, hand);
    }

    /**
     * recognizes the best combination on board, but all inputs must be already VALID !
     *
     * @param board
     * @param hand
     * @return
     * @throws IncorrectBoardException
     */
    private static ComboCardsPair recognizeCombinationOnBoardChecked(Board board, Hand hand)
            throws IncorrectBoardException {
        ArrayList<Card> extendedCards = new ArrayList<Card>(board.getCards());
        if (hand != null) {
            extendedCards.addAll(hand.getCards());
        }

        // Sorts the board by increasing the card rank.
        sortBoard(extendedCards);

        ArrayList<Card> combCards = findBestRoyalFlush(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(FLUSH_ROYAL, combCards);
        }

        combCards = findBestStraightFlush(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.STRAIGHT_FLUSH, combCards);
        }

        combCards = findBestQuads(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.QUADS, combCards);
        }

        combCards = findBestFullHouse(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.FULL_HOUSE, combCards);
        }

        combCards = findBestFlush(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(FLUSH, combCards);
        }

        combCards = findBestStraight(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.STRAIGHT, combCards);
        }

        combCards = findBestSet(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.SET, combCards);
        }

        combCards = findBestTwoPairs(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.TWO_PAIRS, combCards);
        }

        combCards = findBestPair(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.PAIR, combCards);
        }

        combCards = findBestHighCard(extendedCards);
        return new ComboCardsPair(Combination.HIGH_CARD, combCards);
    }

    /**
     * @param extendedCards Checks if there is a Royal flush on the board.
     * @return Board containing the cards of the combination or {@code}null if the combination was not found
     * @throws IncorrectBoardException in case the cards do not form a valid board
     */
    private static ArrayList<Card> findBestRoyalFlush(ArrayList<Card> extendedCards)
            throws IncorrectBoardException {
        Card.Suit majorSuit = countFlushSuit(extendedCards);
        if (majorSuit == null) {
            return null;
        }

        ArrayList<Card> suitedCards = new ArrayList<>();
        for (Card extendedCard : extendedCards) {
            if (extendedCard.getSuit() == majorSuit) {
                suitedCards.add(extendedCard);
            }
        }

        int sum = 0;
        for (Card card : suitedCards) {
            sum += card.getRank().value;
        }
        // A + K + Q + J + T = 14 + 13 + 12 + 11 + 10 = 60
        if (sum == 60) {
            return suitedCards;
        }
        return null;
    }

    /**
     * Checks if there is a straight flush on the board and finds the best one there is.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      *                            for method to work correctly
     * @return Board containing the cards of the combination or {@code null} if the combination was not found
     * @throws IncorrectBoardException in case the cards do not form a valid board
     */
    private static ArrayList<Card> findBestStraightFlush(ArrayList<Card> extendedCards) throws IncorrectBoardException {
        Card.Suit majorSuit = countFlushSuit(extendedCards);
        if (majorSuit == null) {
            return null;
        }

        ArrayList<Card> suitedCards = new ArrayList<>();
        for (Card card : extendedCards) {
            if (card.getSuit() == majorSuit) {
                suitedCards.add(card);
            }
        }

        return findBestStraight(suitedCards);
    }

    /**
     * Checks if there is quads on the board and finds the best one there is.
     *
     * @param sortedExtendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                            for method to work correctly
     * @return Board containing the cards of the combination or {@code null}if the combination was not found
     */
    private static ArrayList<Card> findBestQuads(ArrayList<Card> sortedExtendedCards) {
        int counter = 1;
        for (int i = sortedExtendedCards.size() - 2; i >= 0; --i) {
            if (sortedExtendedCards.get(i).getRank() == sortedExtendedCards.get(i + 1).getRank()) {
                ++counter;
                if (counter == 4) {
                    ArrayList<Card> quadsBoard = new ArrayList<>();
                    for (int qi = i; qi < i + 4; ++qi) {
                        quadsBoard.add(sortedExtendedCards.get(qi));
                    }
                    int lastCardInd = sortedExtendedCards.size() - 1;

                    // Adding kicker to the quads.
                    while (quadsBoard.contains(sortedExtendedCards.get(lastCardInd))) {
                        --lastCardInd;
                    }
                    quadsBoard.add(sortedExtendedCards.get(lastCardInd));
                    return quadsBoard;
                }
            } else {
                counter = 1;
            }
        }
        return null;
    }

    /**
     * Checks if there is a full house on the board and finds the best one there is.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      for method to work correctly
     * @return Board containing the cards of the combination or {@code null} if the combination was not found
     */
    private static ArrayList<Card> findBestFullHouse(ArrayList<Card> extendedCards) {
        boolean isThreeSame = false;
        boolean isTwoSame = false;
        ArrayList<Card> fullHouse = new ArrayList<>();
        int curSame = 1;

        int i = extendedCards.size() - 2;
        while ((!isThreeSame || !isTwoSame) && i >= 0) {
            if (extendedCards.get(i).getRank() == extendedCards.get(i + 1).getRank()) {
                ++curSame;
            } else {
                if (curSame == 2) {
                    if (!isTwoSame) {
                        fullHouse.add(extendedCards.get(i + 1));
                        fullHouse.add(extendedCards.get(i + 2));
                    }
                    isTwoSame = true;
                }
                if (curSame == 3) {
                    if (!isThreeSame) {
                        fullHouse.add(extendedCards.get(i + 1));
                        fullHouse.add(extendedCards.get(i + 2));
                        fullHouse.add(extendedCards.get(i + 3));
                        isThreeSame = true;
                    } else {
                        isTwoSame = true;
                        fullHouse.add(extendedCards.get(i + 1));
                        fullHouse.add(extendedCards.get(i + 2));
                    }
                }
                curSame = 1;
            }
            --i;
        }

        // The case, when the pair or the set part of full-house are the
        // lowest cards on board. Not seen in loop.
        if (curSame == 3 && !isThreeSame) {
            fullHouse.add(extendedCards.get(0));
            fullHouse.add(extendedCards.get(1));
            fullHouse.add(extendedCards.get(2));
            isThreeSame = true;
        }
        // else here is ESSENTIAL. otherwise, the single set will form a full-house
        else if (!isTwoSame && curSame >= 2) {
            fullHouse.add(extendedCards.get(0));
            fullHouse.add(extendedCards.get(1));
            isTwoSame = true;
        }

        if (isThreeSame && isTwoSame) {
            return fullHouse;
        }
        return null;
    }

    /**
     * Checks if there is a flush on the board and finds the best one there is.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      for method to work correctly
     * @return Board containing the cards of the combination or {@code null}if the combination was not found
     */
    private static ArrayList<Card> findBestFlush(ArrayList<Card> extendedCards) throws IncorrectBoardException {
        Card.Suit suit = countFlushSuit(extendedCards);
        if (suit == null) {
            return null;
        }
        int i = extendedCards.size() - 1;
        ArrayList<Card> flushBoard = new ArrayList<>();
        while (flushBoard.size() < 5) {
            if (extendedCards.get(i).getSuit() == suit) {
                flushBoard.add(extendedCards.get(i));
            }
            --i;
        }
        return flushBoard;
    }

    /**
     * Checks if there is a straight on the board and finds the best one there is.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      for method to work correctly
     * @return Board containing the cards of the combination or {@code null}if the combination was not found
     */
    private static ArrayList<Card> findBestStraight(ArrayList<Card> extendedCards) {
        ArrayList<Card> straightBoard = new ArrayList<>();
        int cons = 1;
        // We can check a sequence of cards to be ordered by ascending and have a diff of 1
        // (finding the highest straight) by simply finding the largest 5 cards going
        // right after each other. And since the initial array os already ascending ordered,
        // we can just skip reset the amount counted if one of the cards is not 1 value less than
        // the previous card.
        int i = extendedCards.size() - 2;
        straightBoard.add(extendedCards.get(extendedCards.size() - 1));
        while (i >= 0 && cons != 5) {
            if (extendedCards.get(i).getRank().value == extendedCards.get(i + 1).getRank().value - 1) {
                cons += 1;
                straightBoard.add(extendedCards.get(i));
            } else if (extendedCards.get(i).getRank().value != extendedCards.get(i + 1).getRank().value) {
                cons = 1;
                straightBoard.clear();
                straightBoard.add(extendedCards.get(i));
            }
            --i;
        }

        // "Wheel" straight case (Ace is serving as a one in a (A 2 3 4 5) straight).
        if (cons == 4 && extendedCards.get(0).getRank().value == 2) {
            if (extendedCards.get(extendedCards.size() - 1).getRank().value == Card.Rank.ACE.value) {
                straightBoard.add(extendedCards.get(extendedCards.size() - 1));
                return straightBoard;
            }
        }

        if (cons == 5) {
            return straightBoard;
        }

        return null;
    }

    /**
     * Checks if there is a set on the board and finds the best one there is.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      for method to work correctly
     * @return Board containing the cards of the combination or {@code null} if the combination was not found
     */
    private static ArrayList<Card> findBestSet(ArrayList<Card> extendedCards) {
        int same = 1;
        int i = extendedCards.size() - 2;
        while (same < 3 && i >= 0) {
            if (extendedCards.get(i).getRank() == extendedCards.get(i + 1).getRank()) {
                ++same;
            } else {
                same = 1;
            }
            --i;
        }

        ++i;
        ArrayList<Card> bestSetBoard = new ArrayList<>();
        if (same == 3) {
            bestSetBoard.add(extendedCards.get(i));
            bestSetBoard.add(extendedCards.get(i + 1));
            bestSetBoard.add(extendedCards.get(i + 2));

            i = extendedCards.size() - 1;
            while (bestSetBoard.size() < 5) {
                if (!bestSetBoard.contains(extendedCards.get(i))) {
                    bestSetBoard.add(extendedCards.get(i));
                }
                --i;
            }
            return bestSetBoard;
        }

        return null;
    }

    /**
     * Checks if there are two pairs on the board and finds the best one there is.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      for method to work correctly
     * @return Board containing the cards of the combination or {@code null}if the combination was not found
     */
    private static ArrayList<Card> findBestTwoPairs(ArrayList<Card> extendedCards) {
        ArrayList<Card> bestTwoPairs = new ArrayList<>();

        int i = extendedCards.size() - 2;
        while (bestTwoPairs.size() < 4 && i >= 0) {
            if (extendedCards.get(i).getRank() == extendedCards.get(i + 1).getRank()) {
                bestTwoPairs.add(extendedCards.get(i));
                bestTwoPairs.add(extendedCards.get(i + 1));
            }
            --i;
        }

        i = extendedCards.size() - 1;
        if (bestTwoPairs.size() == 4) {
            while (bestTwoPairs.contains(extendedCards.get(i))) {
                --i;
            }
            bestTwoPairs.add(extendedCards.get(i));
            return bestTwoPairs;
        }
        return null;
    }

    /**
     * Checks if there is a pair on the board and finds the best one there is.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      for method to work correctly
     * @return Board containing the cards of the combination or {@code null}if the combination was not found
     */
    private static ArrayList<Card> findBestPair(ArrayList<Card> extendedCards) {
        int i = extendedCards.size() - 2;
        ArrayList<Card> bestPair = new ArrayList<>();
        while (i >= 0) {
            if (extendedCards.get(i).getRank() == extendedCards.get(i + 1).getRank()) {
                bestPair.add(extendedCards.get(i));
                bestPair.add(extendedCards.get(i + 1));
                break;
            }
            --i;
        }
        if (bestPair.size() == 2) {
            i = extendedCards.size() - 1;
            while (bestPair.size() < 5) {
                if (!bestPair.contains(extendedCards.get(i))) {
                    bestPair.add(extendedCards.get(i));
                }
                --i;
            }
            return bestPair;
        }

        return null;
    }

    /**
     * Returns the best high card combination.
     *
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                      for method to work correctly
     * @return Board containing the cards of the combination.
     */
    private static ArrayList<Card> findBestHighCard(ArrayList<Card> extendedCards) {
        ArrayList<Card> highCardBoard = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            highCardBoard.add(extendedCards.get(extendedCards.size() - i - 1));
        }
        return highCardBoard;
    }

    /**
     * Checks if the flush is present on the given board.
     *
     * @param extendedCards The board that is checked for flush suit.
     * @return suit of cards that make flush, or {@code null} if no flush is present on the board.
     */
    public static Card.Suit countFlushSuit(ArrayList<Card> extendedCards) throws IncorrectBoardException {
        if (!isBoardValid(extendedCards)) {
            throw new IncorrectBoardException("The board is incorrect. It must be valid.");
        }
        int s = 0;
        int c = 0;
        int h = 0;
        int d = 0;

        for (Card card : extendedCards) {
            if (card.getSuit() == Card.Suit.SPADES) {
                s++;
            }
            if (card.getSuit() == Card.Suit.CLUBS) {
                c++;
            }
            if (card.getSuit() == Card.Suit.HEARTS) {
                h++;
            }
            if (card.getSuit() == Card.Suit.DIAMONDS) {
                d++;
            }
        }

        if (s >= 5) {
            return Card.Suit.SPADES;
        } else if (c >= 5) {
            return Card.Suit.CLUBS;
        } else if (h >= 5) {
            return Card.Suit.HEARTS;
        } else if (d >= 5) {
            return Card.Suit.DIAMONDS;
        }
        return null;
    }


    /**
     * Determines which hand is best at showdown (only works for full 5 cards board)
     *
     * @param board valid 5 cards board
     * @param hands arrayList with all the hands that participate in the game
     * @return arrayList of hand that will win at the board (side pots are not considered)
     * @throws IllegalArgumentException if Board consist of less than 5 cards or no hands are given (empty arraylist)
     */
    public static ArrayList<Hand> determineWinningHand(Board board, ArrayList<Hand> hands) throws IncorrectBoardException {
        if (board.size() < 5) {
            throw new IllegalArgumentException("Board must consist of 5 cards");
        }
        if (hands.isEmpty()) {
            throw new IllegalArgumentException("At least 1 hand must be provided for method to work");
        }
        if (hands.size() == 1) {
            return hands;
        }

        ArrayList<Card> cards = new ArrayList<>();
        for (Hand h : hands) {
            cards.add(h.getCard1());
            cards.add(h.getCard2());
        }
        if (!isBoardValid(board, cards)) {
            throw new IllegalArgumentException("Board and hands must contain unique cards");
        }

        return determineWinningHandChecked(board, hands);
    }

    /**
     * determines the hand that will win in given situation, but all inputs must be VALID !
     *
     * @param board
     * @param hands
     * @return
     * @throws IncorrectBoardException
     */
    private static ArrayList<Hand> determineWinningHandChecked(Board board, ArrayList<Hand> hands) throws IncorrectBoardException {
        ArrayList<ComboCardsPair> bestHandsCombos = new ArrayList<>();
        ArrayList<Hand> bestHands = new ArrayList<>();
        int maxCombo = 0;
        for (Hand h : hands) {
            ComboCardsPair c = recognizeCombinationOnBoard(board, h);
            if (maxCombo == c.getCombination().value) {
                bestHandsCombos.add(c);
                bestHands.add(h);
            }
            if (maxCombo < c.getCombination().value) {
                maxCombo = c.getCombination().value;
                bestHandsCombos.clear();
                bestHandsCombos.add(c);

                bestHands.clear();
                bestHands.add(h);
            }
        }
        if (bestHands.size() == 1) {
            return bestHands;
        }
        ArrayList<Card> bestCombo = new ArrayList<>(bestHandsCombos.get(0).getCards());
        sortBoard(bestCombo);

        ArrayList<Hand> winnerHands = new ArrayList<>();
        for (int curComboInd = 0; curComboInd < bestHandsCombos.size(); ++curComboInd) {
            ComboCardsPair ccp = bestHandsCombos.get(curComboInd);
            ArrayList<Card> combinationCards = new ArrayList<>(ccp.getCards());
            sortBoard(combinationCards);
            winnerHands.add(bestHands.get(curComboInd));
            for (int i = 0; i < 5; ++i) {
                if (combinationCards.get(i).getRank().value > bestCombo.get(i).getRank().value) {
                    bestCombo = combinationCards;
                    winnerHands.clear();
                    winnerHands.add(bestHands.get(curComboInd));
                    break;
                } else if (combinationCards.get(i).getRank().value < bestCombo.get(i).getRank().value) {
                    winnerHands.remove(bestHands.get(curComboInd));
                }
            }
        }
        return winnerHands;
    }


    public static HashMap<Hand, Double> countEVPreFlopPrecise(ArrayList<Hand> playersHands) throws IncorrectBoardException {
        Board b;
        HashMap<Hand, Double> boardsWon = new HashMap<>();
        for (int i = 0; i < playersHands.size(); ++i) {
            boardsWon.put(playersHands.get(i), 0.0);
        }

        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 52; ++i) {
            cards.add(new Card(i));
        }

        for (Hand h : playersHands) {
            cards.remove(h.getCard1());
            cards.remove(h.getCard2());
        }

        for (int index1 = 0; index1 < cards.size(); ++index1) {
            Card c1 = cards.get(index1);
            for (int index2 = index1 + 1; index2 < cards.size(); ++index2) {
                Card c2 = cards.get(index2);
                for (int index3 = index2 + 1; index3 < cards.size(); ++index3) {
                    Card c3 = cards.get(index3);
                    for (int index4 = index3 + 1; index4 < cards.size(); ++index4) {
                        Card c4 = cards.get(index4);
                        for (int index5 = index4 + 1; index5 < cards.size(); ++index5) {
                            Card c5 = cards.get(index5);
                            b = new Board(c1, c2, c3, c4, c5);
                            try {
                                ArrayList<Hand> handsWon = determineWinningHand(b, playersHands);
                                for (Hand h : handsWon) {
                                    boardsWon.put(h, boardsWon.get(h) + 1.0 / (double) handsWon.size());
                                }
                            } catch (IncorrectBoardException exception) {
                                // Incorrect board means that cards from the hand were in
                                // generated board, hence we ignore that board (it is impossible to achieve).
                            }
                        }
                    }
                }
            }
        }
        Double sum = 0.0;
        for (Hand h : boardsWon.keySet()) {
            sum += boardsWon.get(h);
        }
        HashMap<Hand, Double> evMap = new HashMap<>();
        for (Hand h : boardsWon.keySet()) {
            evMap.put(h, boardsWon.get(h) / sum);
        }
        return evMap;
    }

    public static HashMap<Hand, Double> countEVPreFlopMonteCarlo(ArrayList<Hand> playersHands) throws IncorrectBoardException {
        ArrayList<Card> cards = new ArrayList<>();
        for (Hand h : playersHands) {
            cards.add(h.getCard1());
            cards.add(h.getCard2());
        }

        if (!isBoardValid(cards)) {
            throw new IllegalArgumentException("Hands must contain unique cards");
        }

        long gamesGenerated = (long) Math.pow(10L, 5);
        Random r = new Random();
        Board b;
        cards = new ArrayList<>();

        for (int i = 0; i < 52; ++i) {
            cards.add(new Card(i));
        }

        for (Hand h : playersHands) {
            cards.remove(h.getCard1());
            cards.remove(h.getCard2());
        }

        HashMap<Hand, Double> boardsWon = new HashMap<>();
        for (Hand h : playersHands) {
            boardsWon.put(h, 0.0);
        }

        HashSet<Card> curBoardCards = new HashSet<>();
        for (int i = 0; i < gamesGenerated; ++i) {
            curBoardCards.clear();

            while (curBoardCards.size() < 5) {
                curBoardCards.add(cards.get(Math.abs(r.nextInt()) % cards.size()));
            }
            b = new Board(new ArrayList<>(curBoardCards));
            try {
                ArrayList<Hand> handsWon = determineWinningHand(b, playersHands);
                for (Hand h : handsWon) {
                    boardsWon.put(h, boardsWon.get(h) + 1.0 / (double) handsWon.size());
                }
            } catch (IncorrectBoardException ex) {
                // Incorrect board means that cards from the hand were in
                // generated board, hence we ignore that board (it is impossible to achieve).
            }
        }

        long gamesPLayed = 0;
        for (Hand h : boardsWon.keySet()) {
            gamesPLayed += boardsWon.get(h);
        }

        HashMap<Hand, Double> evArray = new HashMap<>();
        for (Hand h : boardsWon.keySet()) {
            evArray.put(h, boardsWon.get(h) * 1.0 / gamesPLayed);
        }
        return evArray;
    }


    public static HashMap<Hand, Double> countEVPostFlop(Board board, ArrayList<Hand> playersHands) throws IncorrectBoardException {
        ArrayList<Card> usedCards = new ArrayList<>(board.getCards());
        for (Hand h : playersHands) {
            usedCards.add(h.getCard1());
            usedCards.add(h.getCard2());
        }
        if (!isBoardValid(usedCards)) {
            throw new IllegalArgumentException("Board cards and hands cards must be unique");
        }
        if (board.size() == 5) {
            ArrayList<Hand> wonHands = determineWinningHandChecked(board, playersHands);
            HashMap<Hand, Double> r = new HashMap<>();
            for (Hand h : playersHands) {
                if (wonHands.contains(h)) {
                    r.put(h, 1.0 / wonHands.size());
                } else {
                    r.put(h, 0.0);
                }
            }

            return r;
        }

        ArrayList<Card> leftCards = new ArrayList<>();
        for (int i = 0; i < 51; ++i) {
            leftCards.add(new Card(i));
        }
        for (int i = 0; i < usedCards.size(); ++i) {
            leftCards.remove(usedCards.get(i));
        }

        HashMap<Hand, Double> evMap=  new HashMap<>();
        for (Hand h : playersHands) {
            evMap.put(h, 0.0);
        }
        Board finalBoard = null;
        ArrayList<Card> cards = new ArrayList<>();
        if (board.size() == 3) {
            for (int index1 = 0; index1 < leftCards.size(); ++index1) {
                Card riverCard = leftCards.get(index1);
                for (int index2 = index1 + 1; index2 < leftCards.size(); ++index2) {
                    Card turnCard = leftCards.get(index2);
                    cards.clear();
                    cards.addAll(board.getCards());
                    cards.add(turnCard);
                    cards.add(riverCard);

                    finalBoard = new Board(cards);

                    ArrayList<Hand> wonHands = determineWinningHandChecked(finalBoard, playersHands);

                    for (Hand h : wonHands) {
                        evMap.put(h, evMap.get(h) + 1.0 / wonHands.size());
                    }
                }
            }
        } else {
            for (int index1 = 0; index1 < leftCards.size(); ++index1) {
                cards = new ArrayList<>(board.getCards());
                cards.add(leftCards.get(index1));
                finalBoard = new Board(cards);
                determineWinningHand(finalBoard, playersHands);

                ArrayList<Hand> wonHands = determineWinningHandChecked(finalBoard, playersHands);

                for (Hand h : wonHands) {
                    evMap.put(h, evMap.get(h) + 1.0 / wonHands.size());
                }
            }
        }

        Double sum = 0.0;
        for (Hand h : evMap.keySet()) {
            sum += evMap.get(h);
        }
        for (Hand h : evMap.keySet()) {
            evMap.put(h, evMap.get(h) / sum);
        }

        return evMap;
    }
}
