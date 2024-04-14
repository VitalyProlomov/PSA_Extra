package modelsTests;

import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Card;
import models.Hand;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class HandTest {
    @Test
    public void testHandConstructor() throws IncorrectCardException, IncorrectHandException {
        Card c1 = new Card("Kc");
        Card c2 = new Card("Js");
        Hand h = new Hand(c1, c2);
        assertTrue(h.getCards().contains(c1));
        assertTrue(h.getCards().contains(c2));

        Card cSame = new Card("Kc");
        assertThrows(IncorrectHandException.class, () -> new Hand(c1, cSame));
    }

    @Test
    public void testHandEquals() throws IncorrectCardException, IncorrectHandException {
        Card c1 = new Card("Kc");
        Card c2 = new Card("Js");
        Hand h1 = new Hand(c1, c2);
        Hand h2 = new Hand(c1, new Card("Js"));
        assertEquals(h1, h2);

        Card c3 = new Card("Th");
        Hand h3 = new Hand(c1, c3);
        assertNotEquals(h1, h3);
        assertNotEquals(h2, h3);

        Hand h4 = new Hand(new Card("2h"), new Card("2d"));
        Hand h5 = new Hand(new Card("2d"), new Card("2h"));
        assertEquals(h4, h5);

        Hand h6 = new Hand(new Card("2d"), new Card("2s"));
        assertNotEquals(h5, h6);
        assertNotEquals(h4, h6);
    }

    @Test
    public void testHandToString() throws IncorrectHandException, IncorrectCardException {
        Hand h = new Hand(new Card("9c"), new Card("5d"));
        assertEquals(h.toString(), "[9♣ 5♦]");
    }

    @Test
    public void testHandHashCodeContract() throws IncorrectHandException, IncorrectCardException {
        Hand h = new Hand("As", "Ts");
        Hand h2 = new Hand("Ts", "As");
        assertEquals(h, h2);
        assertEquals(h.hashCode(), h2.hashCode());

        // (As 9h) (Ah 9s)
        Hand hSame = new Hand("As", "Ts");
        assertEquals(h, hSame);
        assertEquals(h.hashCode(), hSame.hashCode());

        h = new Hand(new Card("Js"), new Card( "Jd"));
        h2 = new Hand(h);
        assertEquals(h, h2);
        assertEquals(h, h2);
    }

    @Test
    public void testHandHashCodeCollisions() throws IncorrectCardException, IncorrectHandException {
        ArrayList<Card> allCards = new ArrayList<>();
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

                allCards.add(new Card(repr));
            }
        }

        // Adding all possible hands to the set to make sure that there are no collisions in
        // in the hash functions.
        HashSet<Hand> allHand = new HashSet<>();
        for (int i = 0; i < 52; ++i) {
            for (int j = 0; j < 52; ++j) {
                if (i != j) {
                    allHand.add(new Hand(allCards.get(i), allCards.get(j)));
                }
            }
        }
        // 1326 = (52 * 51) / 2 = total number of different hands in poker.
        // Since every hand was added successfully, that means that every different hand
        // has different hashCode function value. Hence, there are no collisions.
        assertEquals(1326, allHand.size());
    }

}
