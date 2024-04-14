package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;

import java.util.*;

/**
 * Class representing the board in the game. Can contain from 3 to 5 unique cards.
 */
public class Board {
    private final ArrayList<Card> cards = new ArrayList<>();

    /**
     * Constructs the Board with the array of cards (the amount of cards must be in [3; 5])
     *
     * @param cards
     * @throws IncorrectBoardException if the cards given are invalid
     *                                 (not unique or length is not in [3; 5]
     */
    public Board(Card... cards) throws IncorrectBoardException {
        if (cards.length < 3 || cards.length > 5) {
            throw new IncorrectBoardException( "Board must contain from 3 to 5 cards.");
        }
        HashSet<Card> unique = new HashSet<>(List.of(cards));
        if (unique.size() != cards.length) {
            throw new IncorrectBoardException("Board must only contain unique cards");
        }
        Collections.addAll(this.cards, cards);
    }

    /**
     * Constructs the Board with the array of cards generateed by given
     * String card representations (the amount of cards must be in [3; 5])
     *
     * @param cardReps String representations of cards.
     * @throws IncorrectBoardException if the cards given are invalid
     *                                 (not unique or length is not in [3; 5]
     */
    public Board(String... cardReps) throws IncorrectBoardException, IncorrectCardException {
        if (cardReps.length < 3 || cardReps.length > 5) {
            throw new IncorrectBoardException( "Board must contain from 3 to 5 cards.");
        }

        HashSet<Card> unique = new HashSet<Card>();
        for (String rep : cardReps) {
            unique.add(new Card(rep));
        }
        if (unique.size() != cardReps.length) {
            throw new IncorrectBoardException("Board must only contain unique cards");
        }

        for (String rep : cardReps) {
            Card c = new Card(rep);
            this.cards.add(c);
        }
    }

    /**
     * Constructs the Board with the given cards
     *
     * @param cards
     * @throws IncorrectBoardException if the cards given are invalid
     *                                 (not unique or length is not in [3; 5]
     */
    @JsonCreator
    public Board(@JsonProperty("cards") ArrayList<Card> cards) throws IncorrectBoardException {
        if (cards.size() < 3 || cards.size() > 5) {
            throw new IncorrectBoardException( "Board must contain from 3 to 5 cards.");
        }
        HashSet<Card> unique = new HashSet<Card>(cards);
        if (unique.size() != cards.size()) {
            throw new IncorrectBoardException("Board must only contain unique cards");
        }

        this.cards.addAll(cards);
    }

    /**
     * Constructs Board by copying the values of the Board parameter
     * (all links are going to be new)
     *
     * @param copyBoard board to copy from
     */
    public Board(Board copyBoard) {
        this.cards.addAll(copyBoard.getCards());
    }

    public Card get(int index) {
        ArrayList<Card> cardsCopy = getCards();
        return cardsCopy.get(index);
    }


    /**
     * @return copy of array of cards, that this board contains.
     */
    public ArrayList<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public int size() {
        return cards.size();
    }

    /**
     * Board can be equal with another object only if it is another board.
     * The boards are considered equal if the boards contain same amount of same cards.
     * The order of first 3 cards does not matter. The 4th and 5th cards, however,
     * must be the same.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != Board.class) {
            return false;
        }
        if (this.size() != ((Board) obj).size()) {
            return false;
        }

        Board b = ((Board) obj);
        Set<Card> set1 = new HashSet<>(this.cards.subList(0, 3));
        Set<Card> set2 = new HashSet<>(b.cards.subList(0, 3));

        for (Card card1 : set1) {
            set2.remove(card1);
        }

        boolean trSame = true;
        for (int i = 3; i < this.size(); ++i) {
            if (b.size() < i || !this.get(i).equals(b.get(i))) {
                trSame = false;
            }
        }
        return set2.size() == 0 && trSame;
    }

    /**
     * returns hashcode of the Board object - first 3 cards` order does not matter,
     * but 4th and 5th cards order matters.
     * @return
     */
    @Override
    public int hashCode() {
        if (size() == 3) {
            return Objects.hash(new HashSet<>(cards.subList(0, 3)));
        } else if (size() == 4) {
            return Objects.hash(new HashSet<>(cards.subList(0, 3)), get(3));
        }
        return Objects.hash(new HashSet<>(cards.subList(0, 3)), get(3), get(4));
    }

    /**
     * @return string representation of the board
     * Example: (As 4h 5c 6d 7s)
     */
    @Override
    public String toString() {
        return "(" + this.cards + ")";
    }
}
