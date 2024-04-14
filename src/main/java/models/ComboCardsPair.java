package models;

import analizer.Combination;
import analizer.CombinationAnalyzer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The Class for Pair of Combination on board and the cards that make up this combination
 */
public class ComboCardsPair {
    private final Combination combination;
    private final HashSet<Card> cards;

    /**
     * Constructs ComboCardsPair with given parameters.
     * Does not check if the Cards actually make the combination, does not check if cards
     * make up a valid board
     * @param combo Combination on board
     * @param cards Cards that make up a combination
     */
    @JsonCreator
    public ComboCardsPair(@JsonProperty("combination") Combination combo, @JsonProperty("cards") ArrayList<Card> cards) {
        this.combination = combo;
        this.cards = new HashSet<>(cards);
    }

    /**
     * Constructs ComboCardsPair with given parameters.
     * Does not check if the Cards actually make the combination, does not check if cards
     * make up a valid board
     * @param combo Combination on board
     * @param cards Set of cards that make up a combination
     */
    public ComboCardsPair(Combination combo, Set<Card> cards) {
        this.combination = combo;
        this.cards = new HashSet<>(cards);
    }


    /**
     * @return a new hashSet of cards with the same cards as in combination (not a link)
     */
    public HashSet<Card> getCards() {
        return new HashSet<>(cards);
    }

    /**
     * @return combination value
     */
    public Combination getCombination() {
        return combination;
    }

    /**
     * ComboCardsPair is equal to another object only if it is another ComboCardsPair.
     * ComboCardsPairs are considered equal if both combination and cards fields are equal.
     * @param obj object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != ComboCardsPair.class) {
            return false;
        }
        return combination == ((ComboCardsPair)obj).combination &&
                this.cards.equals(((ComboCardsPair)obj).cards);
    }

    /**
     * @return standard hashcode function for ComboCardsPair object
     */
    @Override
    public int hashCode() {
        return Objects.hash(combination, cards);
    }

    /**
     * @return string representation like such: (ComboBoardPair| Combination: x, Cards: y),
     * where x is combination name, y is the list of cards string representations.
     */
    @Override
    public String toString() {
        ArrayList<Card> cardsAr = new ArrayList<>(cards);
        CombinationAnalyzer.sortBoard(cardsAr);
        return "(ComboBoardPair| Combination: " + combination + ", Cards: " + cardsAr + ")";
    }
}
