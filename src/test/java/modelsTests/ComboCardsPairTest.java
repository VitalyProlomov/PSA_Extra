package modelsTests;

import analizer.Combination;
import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import models.Board;
import models.Card;
import models.ComboCardsPair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComboCardsPairTest {
    @Test
    public void testEquals() throws IncorrectBoardException, IncorrectCardException {
        Board br = new Board("2c", "Jd", "Th", "8h", "Ks");
        ArrayList<Card> b = br.getCards();
        Combination p = Combination.PAIR;

        ComboCardsPair cbp = new ComboCardsPair(p, b);
        ArrayList sameCards = new ArrayList(
                List.of(new Card("2c"),
                        new Card("Jd"),
                        new Card("Th"),
                        new Card("8h"),
                        new Card("Ks")
                ));
        ComboCardsPair cbpSame = new ComboCardsPair(p, sameCards);
        assertEquals(cbp, cbpSame);
    }

    @Test
    public void testComboBoardPairToString() throws IncorrectBoardException, IncorrectCardException {
        ArrayList sameCards = new ArrayList(List.of(
                        new Card("5d"),
                        new Card("4c"),
                        new Card("6c"),
                        new Card("7c"),
                        new Card("8h")
                ));
        ComboCardsPair cbp = new ComboCardsPair(Combination.STRAIGHT, sameCards);

        assertEquals("(ComboBoardPair| Combination: STRAIGHT, Cards: [4♣, 5♦, 6♣, 7♣, 8♥])", cbp.toString());
//        System.out.println(cbp.toString());
    }

    @Test
    public void testComboCardsPairHashCode() {

    }
}
