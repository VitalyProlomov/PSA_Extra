package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;

import java.util.HashSet;
import java.util.List;

/**
 * Class that represents Hand of the player (2 unique cards)
 */
public class Hand {
    private final Card card1;
    private final Card card2;

    /**
     * Constructs new Hand with 2 given cards (they must be unique - not equal)
     * @throws IncorrectHandException if cards are equal
     */
    @JsonCreator
    public Hand(@JsonProperty("card1") Card c1, @JsonProperty("card2") Card c2) throws IncorrectHandException {
        if (c1.equals(c2)) {
            throw new IncorrectHandException("Cards must be unique, but was: " + c1 + ", " + c1);
        }
        this.card1 = c1;
        this.card2 = c2;
    }

    /**
     * Constructs new Hand using 2 String representations of cards.
     * @throws IncorrectCardException If either card representation is incorrect
     * @throws IncorrectHandException if cards are equal
     */
    public Hand(String cardRep1, String cardRep2) throws IncorrectCardException, IncorrectHandException {
        Card c1 = new Card(cardRep1);
        Card c2 = new Card(cardRep2);
        if (c1.equals(c2)) {
            throw new IncorrectHandException();
        }
        this.card1 = c1;
        this.card2 = c2;
    }

    /**
     * Constructs new Hand with the same cards as the Hand given as a parameter
     */
    public Hand(Hand another) {
        this.card1 = another.card1;
        this.card2 = another.card2;
    }

    /**
     * @return set of 2 cards in this hand.
     */
    public HashSet<Card> getCards() {
        return new HashSet<>(List.of(card1, card2));
    }

    public Card getCard1() {
        return card1;
    }

    public Card getCard2() {
        return card2;
    }


    /**
     * Hand is equal to another object only if it is another Hand
     * Hands are considered equal if both cards are equal (order does not matter).
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == Hand.class) {
            Hand h = (Hand)obj;
            return (card1.equals(h.card1) && card2.equals(h.card2)) ||
                    (card1.equals(h.card2) && card2.equals(h.card1));
        }
        return false;
    }

    /**
     * Counts the hash function using the following formula:
     * h(card1)^3 + h(card2)^3, where h(x) = x.hashCode().
     * @return hashCode of this object
     */
    @Override
    public int hashCode() {
        return (int)Math.pow(card1.hashCode(), 3) + (int)Math.pow(card2.hashCode(), 3);
    }

    /**
     * Returns string representation of the hand (x, y) as following:
     * [x y], where x and y are cards that the hand is composed of.
     * @return string representation of hand
     */
    @Override
    public String toString() {
        return "[" + this.card1 + " " + this.card2 + "]";
    }
}
