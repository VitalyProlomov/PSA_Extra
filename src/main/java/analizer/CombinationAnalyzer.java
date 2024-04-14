package analizer;

import exceptions.IncorrectBoardException;
import models.Board;
import models.Card;
import models.ComboCardsPair;
import models.Hand;

import java.util.ArrayList;
import java.util.HashSet;

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
     * @throws IncorrectBoardException
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

        // Sorts the board by increasing the card rank.
        sortBoard(extendedCards);

        ArrayList<Card> combCards = findBestRoyalFlush(extendedCards);
        if (combCards != null) {
            return new ComboCardsPair(Combination.FLUSH_ROYAL, combCards);
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
            return new ComboCardsPair(Combination.FLUSH, combCards);
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
        for (int i = 0; i < extendedCards.size(); ++i) {
            if (extendedCards.get(i).getSuit() == majorSuit) {
                suitedCards.add(extendedCards.get(i));
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
     *      *                            for method to work correctly
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
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                            for method to work correctly
     * @return Board containing the cards of the combination or {@code null}if the combination was not found
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
     *                            for method to work correctly
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
     *                            for method to work correctly
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
     *                            for method to work correctly
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
     *                            for method to work correctly
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
     *                            for method to work correctly
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
     * @param extendedCards cards being checked - must be sorted by method {@code sortBoard}
     *                            for method to work correctly
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


}
