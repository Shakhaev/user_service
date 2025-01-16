package school.faang.user_service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

import static school.faang.user_service.constant.Currency.USD;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    ONE_MONTH(30, BigDecimal.valueOf(10L), USD),
    THREE_MONTHS(90, BigDecimal.valueOf(25L), USD),
    ONE_YEAR(365, BigDecimal.valueOf(80L), USD);

    private final Integer days;
    private final BigDecimal price;
    private final Currency currency;

    public static PremiumPeriod fromDays(Integer requestedDays) {
        for (PremiumPeriod premiumPeriod : values()) {
            if (premiumPeriod.days.equals(requestedDays)) {
                return premiumPeriod;
            }
        }
        throw new RuntimeException("No PremiumPeriod by requestedDays");

    }
}
