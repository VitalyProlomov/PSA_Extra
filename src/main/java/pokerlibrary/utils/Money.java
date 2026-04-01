package pokerlibrary.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    private final BigDecimal amount;

    public static Money ZERO = new Money(BigDecimal.ZERO);

    public Money(String money) {
        if (money == null || money.trim().isEmpty()) {
            this.amount = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        } else {
            this.amount = new BigDecimal(money.trim()).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public Money(BigDecimal amount) {
        if (amount == null) {
            this.amount = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        } else {
            this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
    }

    // Factory methods
    public static Money of(String value) { return new Money(value); }
    public static Money of(BigDecimal value) { return new Money(value); }
    public static Money ofCents(long cents) {
        return new Money(BigDecimal.valueOf(cents, 2));
    }

    // Operations
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
    }

    // Accessors
    public BigDecimal toBigDecimal() { return amount; }
    public double toDouble() { return amount.doubleValue(); } // Only for legacy compatibility

    // Comparison
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros().toPlainString());
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }

    public boolean isZero() { return amount.signum() == 0; }
    public boolean isPositive() { return amount.signum() > 0; }
}