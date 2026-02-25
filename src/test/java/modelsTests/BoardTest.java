package modelsTests;

import pokerlibrary.exceptions.IncorrectBoardException;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.models.Board;
import pokerlibrary.models.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    @Test
    public void testBoardConstructor() throws IncorrectCardException, IncorrectBoardException {
        Card c1 = new Card("4c");
        Card c2 = new Card("As");
        Card c3 = new Card("Ks");
        Card c4 = new Card("6h");
        Card c5 = new Card("Td");
        Card c6 = new Card("9d");
        Card c7 = new Card("Js");
        Card c8 = new Card("Qs");

        assertThrows(IncorrectBoardException.class, () -> new Board(c1));
        assertThrows(IncorrectBoardException.class, () -> new Board(c1, c2));
        assertThrows(IncorrectBoardException.class, () -> new Board(c1, c2, c3, c4, c5, c6, c7, c8));

        assertDoesNotThrow(() -> new Board(c1, c2, c3));
        assertDoesNotThrow(() -> new Board(c1, c2, c3, c4));
        assertDoesNotThrow(() -> new Board(c1, c2, c3, c4, c5));

        Board b = new Board(c1, c2, c3, c4, c5);
        ArrayList<Card> actual = b.getCards();
        ArrayList<Card> expected = new ArrayList<>(List.of(new Card[]{c1, c2, c3, c4, c5}));

        assertEquals(actual, expected);
    }

    @Test
    public void testBoardEquals() throws IncorrectBoardException, IncorrectCardException {
        Board a = new Board("3c", "7d", "As");
        Board b = new Board("3c", "7d", "As");
        assertEquals(a, b);

        b = new Board("7d", "As", "3c");
        assertEquals(a, b);

        b = new Board("3c", "7d", "As", "4h");
        assertNotEquals(a, b);

        b = new Board("3c", "7d", "Ad");
        assertNotEquals(a, b);

        b = null;
        assertNotEquals(b, a);

        Object obj = new Object();
        assertNotEquals(a, obj);

        a = new Board("2c", "3c", "4c", "5c", "6c");
        b = new Board("2c", "3c", "4c", "5c", "6c");
        assertEquals(a, b);

        b = new Board("2c", "3c", "4c", "5c");
        assertNotEquals(a, b);
    }

    @Test
    public void testCopyConstructor() throws IncorrectBoardException, IncorrectCardException {
        Board b = new Board("Kc", "Jd", "6s", "As", "2s");
        Board copy = new Board(b);
        assertEquals(b, copy);
    }

    @Test
    public void testIncorrectBoard() throws IncorrectCardException {
        assertThrows(IncorrectBoardException.class, () -> new Board("4c", "7d", "Ks", "2s", "Ks"));
        assertThrows(IncorrectBoardException.class, () -> new Board("Qs", "Qs", "As", "2s", "Ks"));
        assertThrows(IncorrectBoardException.class, () -> new Board(
                new Card("Qs"),
                new Card("Qs"),
                new Card("As"),
                new Card("2s"),
                new Card("Ks")
        ));

        ArrayList<Card> cards = new ArrayList<>(List.of(
                new Card("Js"),
                new Card("Qs"),
                new Card("As"),
                new Card("As"),
                new Card("Ks")
        ));
        assertThrows(IncorrectBoardException.class, () -> new Board(cards));

        assertThrows(IncorrectBoardException.class, () -> new Board("2c", "2c", "3d", "Ad"));

        assertThrows(IncorrectBoardException.class, () ->new Board("5c", "Qd", "2c", "8d", "Qd"));

        assertThrows(IncorrectBoardException.class, () -> new Board("6h", "9d", "8h", "Ts", "Ts"));

        assertDoesNotThrow(() -> new Board("3c", "3s", "3d"));
    }

//    @Test
//    public void testBoardHashCode() throws IncorrectBoardException, IncorrectCardException {
//        Board b = new Board("4c", "Jd", "Qd", "Ad", "2s");
//        Board bSame = new Board("4c", "Jd", "Qd", "Ad", "2s");
//
//        assertEquals(b, bSame);
////        assertEquals();
//    }

}
