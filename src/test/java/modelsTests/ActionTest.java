package modelsTests;

import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import models.Action;
import models.Hand;
import models.PlayerInGame;
import models.PositionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActionTest {
    @Test
    public void testActionConstructor() throws IncorrectHandException, IncorrectCardException {
        PlayerInGame p = new PlayerInGame("b2355knn");
        assertDoesNotThrow(() -> new Action(Action.ActionType.BET, p.getId(), 2.06, 0.35));
        Action act = new Action(Action.ActionType.BET, p.getId(), 2.06, 0.35);
        assertEquals(Action.ActionType.BET, act.getActionType());
        assertEquals(p.getId(), act.getPlayerId());
        assertEquals(2.06, act.getAmount());
        assertEquals(0.35, act.getPotBeforeAction());

        PlayerInGame p2 = new PlayerInGame("g6jj8w90l");
        p2.setHand(new Hand("8h", "Ah"));
        p2.setPosition(PositionType.BB);
        act = new Action(Action.ActionType.CHECK, p2.getId(), 1.0, 5.4);
        assertDoesNotThrow(() -> new Action(Action.ActionType.BET, p2.getId(), 2.06, 0));
        assertEquals(Action.ActionType.CHECK, act.getActionType());
        assertEquals(p2.getId(), act.getPlayerId());
        assertEquals(0, act.getAmount());
        assertEquals(5.4, act.getPotBeforeAction());

        // Raised amount is , 0, which is the mistake, leading to throwing an exception.
        assertThrows(IllegalArgumentException.class, () -> new Action(Action.ActionType.RAISE, p.getId(), -3, 0.9));
        // Called amount must be indicated, amount can not be <= 0
        assertThrows(IllegalArgumentException.class, () -> new Action(Action.ActionType.CALL, p.getId(), 0, 0.3));
        assertThrows(IllegalArgumentException.class, () -> new Action(Action.ActionType.BET, p.getId(), 0, 0));

        assertThrows(IllegalArgumentException.class, () -> new Action(Action.ActionType.BET, p.getId(), 10, -5));
    }

    @Test
    public void testActionToString() {
        PlayerInGame p = new PlayerInGame("345lk");
        Action action = new Action(Action.ActionType.CALL, p.getId(), 1.2, 2.4);
        String correctStr = "(Action| Type: CALL, Amount: 1.20, Pot before action: 2.40, " +
                "Player Id: 345lk)";
        assertEquals(correctStr, action.toString());

        action = new Action(Action.ActionType.CHECK, p.getId(), 0, 3.2);
//        System.out.println(action);
        correctStr = "(Action| Type: CHECK, Pot before action: 3.20, " +
                "Player Id: 345lk)";
        assertEquals(correctStr, action.toString());
    }

    @Test
    public void testActionEquals() {
        PlayerInGame p1 = new PlayerInGame("5d", PositionType.BTN, 54);
        Action a1 = new Action(Action.ActionType.RAISE, p1.getId(), 2.3, 1.4);

        PlayerInGame p2 = new PlayerInGame("5d", PositionType.CO, 44);
        Action a2 = new Action(Action.ActionType.RAISE, p2.getId(), 2.3, 1.4);
        assertEquals(a1, a2);

        a2 = new Action(Action.ActionType.CALL, p2.getId(), 2.3, 1.4);
        assertNotEquals(a1, a2);

        a2 = new Action(Action.ActionType.RAISE, p2.getId(), 1111, 1.4);
        assertNotEquals(a1, a2);

        a2 = new Action(Action.ActionType.RAISE, p2.getId(), 2.3, 1.3);
        assertNotEquals(a1, a2);

        a1 = new Action(Action.ActionType.FOLD, p1.getId(), -2, 0);
        a2 = new Action(Action.ActionType.FOLD, p2.getId(), 0, 0);
        assertEquals(a1, a2);

        a2 = new Action(Action.ActionType.FOLD, p2.getId(), 0, 0.6);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testActionHashCode() {
        PlayerInGame p1 = new PlayerInGame("2wg665k7", PositionType.SB, 100);
        PlayerInGame p2 = new PlayerInGame("34564200");
        PlayerInGame p3 = new PlayerInGame("r4o9pm6", PositionType.BB, 54.44);
        PlayerInGame p4 = new PlayerInGame("q221mmc", PositionType.LJ, 11.9);
        PlayerInGame p4Same = new PlayerInGame("q221mmc");

        Action a1 = new Action(Action.ActionType.CALL, p1.getId(), 1.2, 2.9);
        Action a1Same = new Action(Action.ActionType.CALL, p1.getId(), 1.2, 2.9);
        Action a2 = new Action(Action.ActionType.CALL, p2.getId(), 1.2, 2.9);
        Action a3 = new Action(Action.ActionType.FOLD, p2.getId(), 1.2, 2.9);
        Action a4 = new Action(Action.ActionType.RAISE, p3.getId(), 4.5, 3.1);
        Action a4Same = new Action(Action.ActionType.RAISE, p3.getId(), 4.5, 3.1);

        assertEquals(a1.hashCode(), a1Same.hashCode());
        assertEquals(a4.hashCode(), a4Same.hashCode());

        assertNotEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), a3.hashCode());
        assertNotEquals(a1.hashCode(), a4.hashCode());
        assertNotEquals(a2.hashCode(), a3.hashCode());

        Action a5 = new Action(Action.ActionType.CHECK, p4.getId(), 4, 3.1);
        Action a5Same = new Action(Action.ActionType.CHECK, p4.getId(), -4.5, 3.1);
        assertEquals(a5.hashCode(), a5Same.hashCode());

        Action a5Same2 = new Action(Action.ActionType.CHECK, p4Same.getId(), 0, 3.1);
        assertEquals(a5.hashCode(), a5Same2.hashCode());
    }

    @Test
    public void testActionGetPlayerId() {
        PlayerInGame p = new PlayerInGame("7", PositionType.BTN, 0.9);
        Action a = new Action(Action.ActionType.CHECK, p.getId(), 0, 10);
        assertEquals(p.getId(), a.getPlayerId());

        p = new PlayerInGame("8");
        assertNotEquals(a.getPlayerId(), p.getId());

    }
}
