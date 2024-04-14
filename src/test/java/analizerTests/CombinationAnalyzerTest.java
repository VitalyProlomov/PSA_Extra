package analizerTests;

import analizer.Combination;
import analizer.CombinationAnalyzer;
import exceptions.IncorrectBoardException;
import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Board;
import models.Card;
import models.ComboCardsPair;
import models.Hand;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CombinationAnalyzerTest {
    // region Finding Combination
    @Test
    public void testFlushCountingMethod() throws IncorrectCardException, IncorrectBoardException {
        Board b = new Board("2c", "4c", "8c", "Kc", "Jc");
        assertEquals(CombinationAnalyzer.countFlushSuit(b.getCards()), Card.Suit.CLUBS);

        b = new Board("5d", "Ad", "Kd", "2d", "5s");
        assertNull(CombinationAnalyzer.countFlushSuit(b.getCards()));

        b = new Board("5d", "Ad", "Kd", "2d", "6d");
        assertEquals(CombinationAnalyzer.countFlushSuit(b.getCards()), Card.Suit.DIAMONDS);
    }

    @Test
    public void testFindFLushRoyal() throws IncorrectBoardException, IncorrectCardException {
        Board rf = new Board("Ah", "Kh", "6s", "Ts", "Th");
        Board finalRf = rf;
        assertDoesNotThrow(() -> CombinationAnalyzer.recognizeCombinationOnBoard(finalRf, null));

        rf = new Board("Ts", "As", "Js", "Ks", "Qs");
        Board c = new Board("Ts", "As", "Js", "Ks", "Qs");
        CombinationAnalyzer.sortBoard(c.getCards());
        assertEquals(new ComboCardsPair(Combination.FLUSH_ROYAL, c.getCards()),
                CombinationAnalyzer.recognizeCombinationOnBoard(rf, null));
    }

    @Test
    public void testFindBestStraightFLush() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("3c", "As", "4c", "9h", "5c");
        Hand h = new Hand("6c", "7c");
        ComboCardsPair ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        Board correctCards = new Board("3c", "6c", "4c", "7c", "5c");
        assertEquals(new ComboCardsPair(Combination.STRAIGHT_FLUSH, correctCards.getCards()), ccp);

        b = new Board("Jh", "Qh", "9h", "Th", "8h");
        h = new Hand("Js", "Ah");
        ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        correctCards = new Board("8h", "9h", "Th", "Jh", "Qh");
        assertEquals(new ComboCardsPair(Combination.STRAIGHT_FLUSH, correctCards.getCards()), ccp);

        b = new Board("2h", "Qd", "9d", "Jd", "Td");
        h = new Hand("Kd", "8d");
        ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        correctCards = new Board("Qd", "9d", "Jd", "Td", "Kd");
        assertEquals(new ComboCardsPair(Combination.STRAIGHT_FLUSH, correctCards.getCards()), ccp);

        // Steel wheel
        b = new Board("As", "4s", "Ks", "3s", "Kd");
        h = new Hand("2s", "5s");
        ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        correctCards = new Board("As", "2s", "3s", "4s", "5s");
        assertEquals(new ComboCardsPair(Combination.STRAIGHT_FLUSH, correctCards.getCards()), ccp);
    }

    @Test
    public void testFindBestQuads() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("8c", "Ad", "2h", "3c", "8d");
        Hand h = new Hand("8h", "8s");
        ComboCardsPair ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        Board correctCards = new Board("8h", "8c", "8d", "8s", "Ad");
        assertEquals(new ComboCardsPair(Combination.QUADS, correctCards.getCards()), ccp);

        b = new Board("As", "Ad", "Ah", "4s", "6h");
        h = new Hand("Ac", "Tc");
        ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        correctCards = new Board("Ah", "Ac", "Ad", "As", "Tc");
        assertEquals(new ComboCardsPair(Combination.QUADS, correctCards.getCards()), ccp);

        b = new Board("Ks", "Kd", "Kh", "Kc", "Qh");
        h = new Hand("Td", "Tc");
        ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        correctCards = new Board("Ks", "Kd", "Kh", "Kc", "Qh");
        assertEquals(new ComboCardsPair(Combination.QUADS, correctCards.getCards()), ccp);

        b = new Board("2s", "2d", "2h", "2c", "Ah");
        h = new Hand("Ad", "Ac");
        ccp = CombinationAnalyzer.recognizeCombinationOnBoard(b, h);
        correctCards = new Board("2s", "2d", "2h", "2c", "Ad");
        assertEquals(new ComboCardsPair(Combination.QUADS, correctCards.getCards()), ccp);
    }

    @Test
    public void testFindBestFullHouse() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("Ah", "Jd", "Js", "6d", "As");
        Hand h = new Hand("Ad", "Jc");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("Ah", "Jd", "Js", "Ad", "As").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.FULL_HOUSE, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("6s", "6d", "6c", "As", "Ah");
        h = new Hand("Ad", "Jc");
        correctCards = new ArrayList<>(new Board("Ah", "Ad", "As", "6d", "6s").getCards());
        ccp = new ComboCardsPair(Combination.FULL_HOUSE, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("2s", "Kd", "4s", "5d", "2d");
        h = new Hand("Kc", "2c");
        correctCards = new ArrayList<>(new Board("Kd", "Kc", "2s", "2d", "2c").getCards());
        ccp = new ComboCardsPair(Combination.FULL_HOUSE, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("7h", "9d", "8s", "5d", "9s");
        h = new Hand("7c", "7d");
        correctCards = new ArrayList<>(new Board("9s", "9d", "7h", "7d", "7c").getCards());
        ccp = new ComboCardsPair(Combination.FULL_HOUSE, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("4s", "4d", "3s", "3d", "Ad");
        h = new Hand("Ac", "Ah");
        correctCards = new ArrayList<>(new Board("Ad", "Ac", "Ah", "4d", "4s").getCards());
        ccp = new ComboCardsPair(Combination.FULL_HOUSE, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));
    }

    @Test
    public void testFindBestFlush() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("Ah", "Jh", "5s", "6h", "As");
        Hand h = new Hand("2h", "3h");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("Ah", "Jh", "3h", "2h", "6h").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.FLUSH, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("5d", "4d", "Kd", "7d", "Td");
        h = new Hand("As", "Jc");
        correctCards = new ArrayList<>(new Board("5d", "4d", "Kd", "7d", "Td").getCards());
        ccp = new ComboCardsPair(Combination.FLUSH, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("5s", "8s", "2s", "7s", "Td");
        h = new Hand("As", "Jc");
        correctCards = new ArrayList<>(new Board("5s", "8s", "2s", "7s", "As").getCards());
        ccp = new ComboCardsPair(Combination.FLUSH, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Qc", "8c", "4c", "Tc", "Ac");
        h = new Hand("3c", "Jc");
        correctCards = new ArrayList<>(new Board("Qc", "8c", "Jc", "Tc", "Ac").getCards());
        ccp = new ComboCardsPair(Combination.FLUSH, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));
    }

    @Test
    public void findBestStraight() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("Ah", "Jh", "5s", "4h", "As");
        Hand h = new Hand("2h", "3d");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("Ah", "2h", "3d", "4h", "5s").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("5d", "4d", "6d", "7d", "Ts");
        h = new Hand("8s", "Jc");
        correctCards = new ArrayList<>(new Board("5d", "4d", "6d", "7d", "8s").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Ad", "2s", "5d", "6h", "3c");
        h = new Hand("4s", "Kc");
        correctCards = new ArrayList<>(new Board("5d", "4s", "6h", "2s", "3c").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Ad", "Ks", "Td", "6h", "3c");
        h = new Hand("Qs", "Js");
        correctCards = new ArrayList<>(new Board("Ad", "Qs", "Js", "Ks", "Td").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Td", "8s", "7h", "2h", "2c");
        h = new Hand("9s", "Js");
        correctCards = new ArrayList<>(new Board("Td", "8s", "7h", "9s", "Js").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Qd", "Js", "Jd", "Th", "9c");
        h = new Hand("8s", "Jc");
        correctCards = new ArrayList<>(new Board("Qd", "Jc", "Th", "9c", "8s").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Qd", "Js", "Jd", "Th", "9c");
        h = new Hand("8s", "7h");
        correctCards = new ArrayList<>(new Board("Qd", "Js", "Th", "9c", "8s").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("2d", "3s", "4c", "5d", "Jc");
        h = new Hand("Ad", "Qh");
        correctCards = new ArrayList<>(new Board("2d", "3s", "4c", "5d", "Ad").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("2d", "3s", "4c", "5d", "Jc");
        h = new Hand("Ad", "6h");
        correctCards = new ArrayList<>(new Board("2d", "3s", "4c", "5d", "6h").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));
    }

    @Test
    public void testFindBestSet() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("Ah", "As", "5s", "4h", "9c");
        Hand h = new Hand("2d", "Ad");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("Ah", "As", "Ad", "9c", "5s").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.SET, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("5c", "4d", "Ks", "Qs", "Ts");
        h = new Hand("5h", "5d");
        correctCards = new ArrayList<>(new Board("5d", "5h", "5c", "Ks", "Qs").getCards());
        ccp = new ComboCardsPair(Combination.SET, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Ad", "Ks", "Kd", "Kc", "8d");
        h = new Hand("Jh", "5d");
        correctCards = new ArrayList<>(new Board("Kd", "Ks", "Kc", "Ad", "Jh").getCards());
        ccp = new ComboCardsPair(Combination.SET, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Qd", "Qc", "Qs", "Ts", "2d");
        h = new Hand("8d", "5d");
        correctCards = new ArrayList<>(new Board("Qd", "Qs", "Qc", "Ts", "8d").getCards());
        ccp = new ComboCardsPair(Combination.SET, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));
    }

    @Test
    public void testFindBestTwoPairs() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("As", "Ks", "9d", "2s", "Qc");
        Hand h = new Hand("2d", "Ad");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("Ad", "As", "2d", "2s", "Ks").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.TWO_PAIRS, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Kc", "4d", "Ks", "Qs", "Ts");
        h = new Hand("5h", "5d");
        correctCards = new ArrayList<>(new Board("5d", "5h", "Kc", "Ks", "Qs").getCards());
        ccp = new ComboCardsPair(Combination.TWO_PAIRS, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Td", "Th", "As", "Ad", "8h");
        h = new Hand("5h", "5d");
        correctCards = new ArrayList<>(new Board("Td", "Th", "As", "Ad", "8h").getCards());
        ccp = new ComboCardsPair(Combination.TWO_PAIRS, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Td", "Th", "2s", "2d", "8h");
        h = new Hand("5h", "5d");
        correctCards = new ArrayList<>(new Board("Td", "Th", "5h", "5d", "8h").getCards());
        ccp = new ComboCardsPair(Combination.TWO_PAIRS, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Td", "2h", "As", "Jd", "8h");
        h = new Hand("Th", "Ah");
        correctCards = new ArrayList<>(new Board("Td", "Th", "As", "Ah", "Jd").getCards());
        ccp = new ComboCardsPair(Combination.TWO_PAIRS, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));
    }

    @Test
    public void testFindBestPair() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("As", "Ks", "9d", "2s", "Qc");
        Hand h = new Hand("Ad", "Jc");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("Ad", "As", "Ks", "Qc", "Jc").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.PAIR, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Kc", "4d", "7s", "Qs", "Ts");
        h = new Hand("5h", "5d");
        correctCards = new ArrayList<>(new Board("5d", "5h", "Kc", "Ts", "Qs").getCards());
        ccp = new ComboCardsPair(Combination.PAIR, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Qs", "8d", "8s", "Js", "2d");
        h = new Hand("5h", "4h");
        correctCards = new ArrayList<>(new Board("Qs", "Js", "8d", "8s", "5h").getCards());
        ccp = new ComboCardsPair(Combination.PAIR, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("9s", "Td", "Ks", "As", "Qd");
        h = new Hand("Th", "2h");
        correctCards = new ArrayList<>(new Board("Qd", "As", "Ks", "Th", "Td").getCards());
        ccp = new ComboCardsPair(Combination.PAIR, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("2s", "8d", "4s", "7s", "Qd");
        h = new Hand("Jh", "2h");
        correctCards = new ArrayList<>(new Board("2s", "2h", "Jh", "Qd", "8d").getCards());
        ccp = new ComboCardsPair(Combination.PAIR, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));
    }

    @Test
    public void testFindBestHighCard() throws IncorrectBoardException, IncorrectCardException, IncorrectHandException {
        Board b = new Board("7s", "3s", "6d", "2s", "Qc");
        Hand h = new Hand("8d", "9d");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("Qc", "9d", "8d", "7s", "6d").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.HIGH_CARD, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Ts", "8d", "2s", "Kd", "3s");
        h = new Hand("5h", "4h");
        correctCards = new ArrayList<>(new Board("Ts", "8d", "5h", "Kd", "4h").getCards());
        ccp = new ComboCardsPair(Combination.HIGH_CARD, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Jd", "8d", "7d", "Ah", "Kh");
        h = new Hand("9d", "2s");
        correctCards = new ArrayList<>(new Board("Ah", "Kh", "Jd", "9d", "8d").getCards());
        ccp = new ComboCardsPair(Combination.HIGH_CARD, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Qs", "2d", "3c", "8s", "9h");
        h = new Hand("Kd", "Th");
        correctCards = new ArrayList<>(new Board("Kd", "Qs", "Th", "8s", "9h").getCards());
        ccp = new ComboCardsPair(Combination.HIGH_CARD, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("As", "Ks", "Qs", "Js", "2d");
        h = new Hand("7d", "9d");
        correctCards = new ArrayList<>(new Board("As", "Ks", "Qs", "Js", "9d").getCards());
        ccp = new ComboCardsPair(Combination.HIGH_CARD, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));
    }

    @Test
    public void testFindingRandomCombinations() throws IncorrectHandException, IncorrectCardException, IncorrectBoardException {
        Board b = new Board("Ks", "7d", "5d", "8d", "2h");
        Hand h = new Hand("6d", "9d");
        ArrayList<Card> correctCards = new ArrayList<>(new Board("7d", "5d", "8d", "6d", "9d").getCards());
        ComboCardsPair ccp = new ComboCardsPair(Combination.STRAIGHT_FLUSH, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("As", "Ad", "Ah", "2d", "5d");
        h = new Hand("5s", "6s");
        correctCards = new ArrayList<>(new Board("As", "Ad", "Ah", "5d", "5s").getCards());
        ccp = new ComboCardsPair(Combination.FULL_HOUSE, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Js", "Td", "9h", "9c", "8h");
        h = new Hand("Jd", "9s");
        correctCards = new ArrayList<>(new Board("Js", "Jd", "9s", "9c", "9h").getCards());
        ccp = new ComboCardsPair(Combination.FULL_HOUSE, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("3c", "4c", "Ks", "7d", "Kd");
        h = new Hand("6s", "5h");
        correctCards = new ArrayList<>(new Board("3c", "4c", "6s", "5h", "7d").getCards());
        ccp = new ComboCardsPair(Combination.STRAIGHT, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("3c", "4c", "Kc", "7d", "Kd");
        h = new Hand("6c", "5c");
        correctCards = new ArrayList<>(new Board("3c", "4c", "6c", "5c", "Kc").getCards());
        ccp = new ComboCardsPair(Combination.FLUSH, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("7d", "9s", "Ah", "Ad", "2c");
        h = new Hand("7h", "2h");
        correctCards = new ArrayList<>(new Board("Ah", "Ad", "7h", "7d", "9s").getCards());
        ccp = new ComboCardsPair(Combination.TWO_PAIRS, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("Kd", "4d", "2d", "5h", "7s");
        h = new Hand("Ah", "4h");
        correctCards = new ArrayList<>(new Board("Kd", "4d", "Ah", "4h", "7s").getCards());
        ccp = new ComboCardsPair(Combination.PAIR, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

        b = new Board("4d", "4s", "4h", "9h", "7s");
        h = new Hand("Ah", "5d");
        correctCards = new ArrayList<>(new Board("Ah", "4d", "4s", "4h", "9h").getCards());
        ccp = new ComboCardsPair(Combination.SET, correctCards);
        assertEquals(ccp, CombinationAnalyzer.recognizeCombinationOnBoard(b, h));

    }
    //endregion

    //region Board Validation method
    @Test
    public void testBoardValid() throws IncorrectBoardException, IncorrectCardException {
        Card As = new Card("As");
        Card Ks = new Card("Ks");
        Card Qs = new Card("Qs");
        Card Js = new Card("Js");
        Card Ts = new Card("Ts");
        Card NineH = new Card("9h");
        Card EightH = new Card("8h");
        Card EightC = new Card("8c");

        ArrayList<Card> invalid = new ArrayList<>(List.of(As, Ks, Qs, Qs, Ts));
        assertFalse(CombinationAnalyzer.isBoardValid(invalid));

        invalid = new ArrayList<>(List.of(As, Js, Js, NineH));
        assertFalse(CombinationAnalyzer.isBoardValid(invalid));

        invalid = new ArrayList<>(List.of(EightC, As, EightC));
        assertFalse(CombinationAnalyzer.isBoardValid(invalid));

        ArrayList<Card> valid = new ArrayList<>(List.of(As, Ks, Qs, Js, Ts));
        assertTrue(CombinationAnalyzer.isBoardValid(valid));

        valid = new ArrayList<>(List.of(EightH, EightC, Js, Ks, Qs));
        assertTrue(CombinationAnalyzer.isBoardValid(valid));
    }
    //endregion

    //region Board Sorting
    @Test
    public void testSortBoardW2SameCards() throws IncorrectBoardException {
        Card H4 = new Card(Card.Rank.FOUR, Card.Suit.HEARTS);
        Card HA = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        Card SK = new Card(Card.Rank.KING, Card.Suit.SPADES);
        Card C4 = new Card(Card.Rank.FOUR, Card.Suit.CLUBS);
        Card DJ = new Card(Card.Rank.JACK, Card.Suit.DIAMONDS);

        Board sortedBoard = new Board(H4, HA, SK, C4, DJ);
        ArrayList<Card> cards1 = sortedBoard.getCards();
        CombinationAnalyzer.sortBoard(cards1);
        Board expectedBoard = new Board(H4, C4, DJ, SK, HA);
        ArrayList<Card> expCards = expectedBoard.getCards();

        assertEquals(expCards, cards1);
    }

    @Test
    public void testSortBoardOnSortedBoard() throws IncorrectCardException, IncorrectBoardException {
        Card h2 = new Card("2h");
        Card s2 = new Card("2s");
        Card d2 = new Card("2d");
        Card c2 = new Card("2c");
        Card sA = new Card("As");

        Board board = new Board(h2, s2, d2, c2, sA);
        ArrayList<Card> cards1 = board.getCards();

        CombinationAnalyzer.sortBoard(cards1);
        Board expected = new Board(h2, s2, d2, c2, sA);
        ArrayList<Card> expCards = expected.getCards();
        assertEquals(cards1, expCards);
    }

    @Test
    public void testSortRandomBoards() throws IncorrectCardException, IncorrectBoardException {
        Card Jh = new Card(Card.Rank.JACK, Card.Suit.HEARTS);
        Card twoS = new Card(Card.Rank.TWO, Card.Suit.SPADES);
        Card threeS = new Card(Card.Rank.THREE, Card.Suit.SPADES);
        Card fourS = new Card(Card.Rank.FOUR, Card.Suit.SPADES);
        Card fiveS = new Card(Card.Rank.FIVE, Card.Suit.SPADES);

        Board board1 = new Board(Jh, twoS, threeS, fourS, fiveS);
        ArrayList<Card> cards1 = board1.getCards();
        CombinationAnalyzer.sortBoard(cards1);

        Board expected1 = new Board(twoS, threeS, fourS, fiveS, Jh);
        ArrayList<Card> expCards = expected1.getCards();
        assertEquals(cards1, expCards);

        Card As = new Card(Card.Rank.ACE, Card.Suit.SPADES);
        Card Td = new Card(Card.Rank.TEN, Card.Suit.DIAMONDS);
        Card nineC = new Card(Card.Rank.NINE, Card.Suit.CLUBS);
        Card sixC = new Card(Card.Rank.SIX, Card.Suit.CLUBS);
        Card fourD = new Card(Card.Rank.FOUR, Card.Suit.DIAMONDS);

        Board board2 = new Board(As, Td, nineC, sixC, fourD);
        ArrayList<Card> cards2 = board2.getCards();
        CombinationAnalyzer.sortBoard(cards2);

        Board expected2 = new Board(fourD, sixC, nineC, Td, As);
        ArrayList<Card> expCards2 = expected2.getCards();
        assertEquals(cards2, expCards2);

        Card sixD = new Card("6d");
        Card nineD = new Card("9d");
        Card fiveD = new Card("5d");
        Card Ad = new Card("ad");
        Card threeD = new Card("3d");

        Board board3 = new Board(sixC, nineD, fiveD, Ad, threeD);
        ArrayList<Card> cards3 = board3.getCards();
        CombinationAnalyzer.sortBoard(cards3);

        Board expected3 = new Board(threeD, fiveD, sixC, nineD, Ad);
        ArrayList<Card> expCards3 = expected3.getCards();
        assertEquals(cards3, expCards3);
    }
    // endregion
}
