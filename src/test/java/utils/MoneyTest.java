package utils;

import org.junit.jupiter.api.Test;
import pokerlibrary.utils.Money;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTest {

    @Test
    public void testConvertingMoney() {
        Money money1 = new Money("45.22");
        Money money2 = new Money("45,22");
        assertEquals(money1, money2);
    }
}
