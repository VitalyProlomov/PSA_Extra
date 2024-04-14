package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import exceptions.IncorrectCardException;

import java.util.Locale;

/**
 * The class for Card entity. Card has a rank and a suit.
 */
public class Card {
    private final Rank rank;
    private final Suit suit;

    /**
     * Creates new instance of the Card with given Rank and Suit.
     * @param rank rank of the created card (could be 2 .. 9, or T, J, Q, K, A)
     * @param suit suit of the created card (could be one of the 4 possible suits)
     */
    @JsonCreator
    public Card(@JsonProperty("rank") Rank rank, @JsonProperty("suit") Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Constructs the Card by its String representation
     *
     * @param strRepresentation String, that consists of the rank and the suit - both
     *                          represented by 1 character (standard notation is used)
     * @throws IncorrectCardException if the parameter is incorrect (either rank or suit are misspelled)
     */
    public Card(String strRepresentation) throws IncorrectCardException {
        if (strRepresentation.length() != 2) {
            throw new IncorrectCardException("Was given : " + strRepresentation + ". Representation of the card must be [Rank][suit], 10 being T," +
                    " suit = 1st letter (ex: 4 of spades = 4s)");
        }

        char valueChar = strRepresentation.toUpperCase(Locale.ROOT).charAt(0);

        if (valueChar == 'T') {
            this.rank = Rank.TEN;
        } else if (valueChar == 'J') {
            this.rank = Rank.JACK;
        } else if (valueChar == 'Q') {
            this.rank = Rank.QUEEN;
        } else if (valueChar == 'K') {
            this.rank = Rank.KING;
        } else if (valueChar == 'A') {
            this.rank = Rank.ACE;
        } else {
            if (valueChar > '9' || valueChar < '2') {
                throw new IncorrectCardException("Representation of the card must be [Rank][suit], 10 being T," +
                        " suit = 1st letter (ex: 4 of spades = 4s)");
            } else {
                int number = valueChar - '0';
                Rank rankToGive = Rank.TWO;
                for (Rank r : Rank.values()) {
                    if (number == r.value) {
                        rankToGive = r;
                    }
                }
                this.rank = rankToGive;
                // Just a check, to make sure my sloppy solution to
                // prevent error popping out (rankToGive) is working correctly
                if (rank == Rank.TWO && (number != 2 || valueChar != '2')) {
                    throw new RuntimeException("I have wrote the code incorrectly: " +
                            "Change the Card constructor (for some reason the rank is " +
                            "assigned incorrectly.");
                }
            }
        }

        char suitChar = strRepresentation.toLowerCase(Locale.ROOT).charAt(1);
        if (suitChar == 's') {
            suit = Suit.SPADES;
        } else if (suitChar == 'c') {
            suit = Suit.CLUBS;
        } else if (suitChar == 'h') {
            suit = Suit.HEARTS;
        } else if (suitChar == 'd') {
            suit = Suit.DIAMONDS;
        } else {
            if (suitChar == '♠') {
                suit = Suit.SPADES;
            } else if (suitChar == '♥') {
                suit = Suit.HEARTS;
            } else if (suitChar == '♦') {
                suit = Suit.DIAMONDS;
            } else if (suitChar == '♣') {
                suit = Suit.CLUBS;
            } else {
                throw new IncorrectCardException("Incorrect Representation: '" + strRepresentation + "'. Representation of the card must be [Rank][suit], 10 being T," +
                        " suit = 1st letter (ex: 4 of spades = 4s)");
            }
        }
    }


    /**
     * Suit of the card. Has a field w char icon.
     */
    public enum Suit {
        HEARTS('♥'),
        SPADES('♠'),
        DIAMONDS('♦'),
        CLUBS('♣');


        public final char icon;

        Suit(char icon) {
            this.icon = icon;
        }

    }

    /**
     * Rank of the card - value is the int number, increasing by one each next rank.
     */
    public enum Rank {
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);

        public final int value;

        Rank(int value) {
            this.value = value;
        }
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }


    /**
     * Card is equal to another object only if it is another Card.
     * Cards are considered equal if both Rank and a Suit are equal.
     *
     * @param obj comapred object
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
        if (obj.getClass() == Card.class) {
            return this.rank == ((Card) obj).rank && this.suit == ((Card) obj).suit;
        }
        return false;
    }

    /**
     * @return hashcode of the Card object. Every one of the 52 possible cards is
     * assigned a unique hashcode - a number between 1 and 52.
     */
    @Override
    public int hashCode() {
        int suitHash = 0;
        if (suit == Suit.HEARTS) {
            suitHash = 1;
        } else if (suit == Suit.CLUBS) {
            suitHash = 2;
        } else if (suit == Suit.SPADES) {
            suitHash = 3;
        }
        return (suitHash * 13) + rank.value - 1;
    }

    /**
     * @return a string representation of the card: [Rank][Suit]
     * Rank is a number if it is in [2; 9], and a letter otherwise,
     * suit is the char icon, assigned to the suit.
     */
    @Override
    public String toString() {
        String rankRepr = "" + rank.value;
        if (rank.value == 10) {
            rankRepr = "T";
        } else if (rank.value == 11) {
            rankRepr = "J";
        } else if (rank.value == 12) {
            rankRepr = "Q";
        } else if (rank.value == 13) {
            rankRepr = "K";
        } else if (rank.value == 14) {
            rankRepr = "A";
        }

        return rankRepr + this.suit.icon;
    }
}
