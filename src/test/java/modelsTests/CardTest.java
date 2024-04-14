package modelsTests;

import exceptions.IncorrectCardException;
import models.Card;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    @Test
    public void testCardInitialization() {
        Card card1 = new Card(Card.Rank.NINE, Card.Suit.SPADES);
        Card card2 = new Card(Card.Rank.KING, Card.Suit.HEARTS);

        assertEquals(Card.Rank.NINE, card1.getRank());
        assertEquals(Card.Suit.SPADES, card1.getSuit());

        assertEquals(Card.Rank.KING, card2.getRank());
        assertEquals(Card.Suit.HEARTS, card2.getSuit());
    }

    @Test
    public void testCardEquals() {
        Card card = new Card(Card.Rank.ACE, Card.Suit.SPADES);
        Card sameCars = new Card(Card.Rank.ACE, Card.Suit.SPADES);
        Card diffCard1 = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        Card diffCard2 = new Card(Card.Rank.QUEEN, Card.Suit.SPADES);
        Card diffCard3 = new Card(Card.Rank.NINE, Card.Suit.CLUBS);

        assertEquals(card, sameCars);
        assertNotEquals(card, diffCard1);
        assertNotEquals(card, diffCard2);
        assertNotEquals(card, diffCard3);

        card = new Card(Card.Rank.EIGHT, Card.Suit.DIAMONDS);
        sameCars = new Card(Card.Rank.EIGHT, Card.Suit.DIAMONDS);
        diffCard1 = new Card(Card.Rank.JACK, Card.Suit.DIAMONDS);
        diffCard2 = new Card(Card.Rank.EIGHT, Card.Suit.SPADES);
        diffCard3 = new Card(Card.Rank.SIX, Card.Suit.CLUBS);

        assertEquals(card, sameCars);
        assertNotEquals(card, diffCard1);
        assertNotEquals(card, diffCard2);
        assertNotEquals(card, diffCard3);
    }

    @Test
    public void testCorrectCardInitializationWStrRepresentation() throws IncorrectCardException {
        Card Ah = new Card("Ah");
        Card Kc = new Card("Kc");
        Card Jd = new Card("Jd");
        Card JD = new Card("JD");
        Card jd = new Card("jd");
        Card eightS = new Card("8S");
        Card eights = new Card("8s");
        Card twoH = new Card("2H");
        Card Td = new Card("Td");


        assertEquals(Ah, new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        assertEquals(Kc, new Card(Card.Rank.KING, Card.Suit.CLUBS));
        assertEquals(Jd, new Card(Card.Rank.JACK, Card.Suit.DIAMONDS));
        assertEquals(JD, new Card(Card.Rank.JACK, Card.Suit.DIAMONDS));
        assertEquals(jd, new Card(Card.Rank.JACK, Card.Suit.DIAMONDS));
        assertEquals(eightS, new Card(Card.Rank.EIGHT, Card.Suit.SPADES));
        assertEquals(eights, new Card(Card.Rank.EIGHT, Card.Suit.SPADES));
        assertEquals(twoH, new Card(Card.Rank.TWO, Card.Suit.HEARTS));
        assertEquals(Td, new Card(Card.Rank.TEN, Card.Suit.DIAMONDS));
    }

    @Test
    public void testIncorrectCardsInitializations() {
        assertThrows(IncorrectCardException.class, () -> new Card("10h"));
        assertThrows(IncorrectCardException.class, () -> new Card("15d"));
        assertThrows(IncorrectCardException.class, () -> new Card("11s"));
        assertThrows(IncorrectCardException.class, () -> new Card("9i"));
        assertThrows(IncorrectCardException.class, () -> new Card("8"));
        assertThrows(IncorrectCardException.class, () -> new Card("Ae"));
        assertThrows(IncorrectCardException.class, () -> new Card("J2"));
        assertThrows(IncorrectCardException.class, () -> new Card("Two of clubs"));
        assertThrows(IncorrectCardException.class, () -> new Card("0d"));
    }

    @Test
    public void checkGetterIsCorrectTest() throws IncorrectCardException {
        Card c = new Card("Js");
        Card.Rank r = c.getRank();
        r = Card.Rank.ACE;
        assertNotEquals(Card.Rank.ACE, c.getRank());
    }

    @Test
    public void testStringConstuctor() throws IncorrectCardException {
        Card twoH = new Card("2h");
        Card threeH = new Card("3h");
        Card fourD = new Card("4d");
        Card fiveC = new Card("5c");
        Card sixC = new Card("6C");
        Card sevenS = new Card("7S");
        Card eightS = new Card("8s");

        assertEquals(twoH, new Card(Card.Rank.TWO, Card.Suit.HEARTS));
        assertEquals(threeH, new Card(Card.Rank.THREE, Card.Suit.HEARTS));
        assertEquals(fourD, new Card(Card.Rank.FOUR, Card.Suit.DIAMONDS));
        assertEquals(fiveC, new Card(Card.Rank.FIVE, Card.Suit.CLUBS));
        assertEquals(sixC, new Card(Card.Rank.SIX, Card.Suit.CLUBS));
        assertEquals(sevenS, new Card(Card.Rank.SEVEN, Card.Suit.SPADES));
        assertEquals(eightS, new Card(Card.Rank.EIGHT, Card.Suit.SPADES));
    }

    @Test
    public void testHashCode() throws IncorrectCardException {

        char[] symbols = new char[] {'♦', '♥', '♣', '♠'};
        int hash = 1;
        for (char j : symbols) {
            for (int i = 2; i <= 14; ++i) {
                String repr = String.valueOf(i);
                if (i == 10) {
                    repr = "T";
                } else if (i == 11) {
                    repr = "J";
                } else if (i == 12) {
                    repr = "Q";
                } else if (i == 13) {
                    repr = "K";
                } else if (i == 14) {
                    repr = "A";
                }

                repr += j;

                assertEquals(hash++, (new Card(repr).hashCode()));
//                System.out.println(new Card(repr).toString() + ", Hash: " + new Card(repr).hashCode());
            }
        }
    }

//    @Test
//    public void equalsHashCodeContracts() {
//        EqualsVerifier.forClass(Card.class).verify();
//    }
}