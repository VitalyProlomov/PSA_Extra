package web.persistence.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import pokerlibrary.utils.Money;

@Converter(autoApply = true) // autoApply = true means it applies to ALL Money fields automatically
public class MoneyConverter implements AttributeConverter<Money, String> {

    @Override
    public String convertToDatabaseColumn(Money money) {
        if (money == null) {
            return null;
        }
        // Store as plain string without $ prefix for cleaner DB storage
        return money.toBigDecimal().toPlainString();
    }

    @Override
    public Money convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Money.of("0");
        }
        return Money.of(dbData);
    }
}