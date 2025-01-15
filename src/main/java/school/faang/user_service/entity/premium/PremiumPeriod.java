package school.faang.user_service.entity.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.payment_service.Currency;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    ONE_MONTH(30, BigDecimal.valueOf(10.0), Currency.USD),
    THREE_MONTHS(90, BigDecimal.valueOf(25.0), Currency.USD),
    ONE_YEAR(365, BigDecimal.valueOf(80.0), Currency.USD);

    private final int days;
    private final BigDecimal price;
    private final Currency currency;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
          case 30 -> ONE_MONTH;
          case 90 -> THREE_MONTHS;
          case 365 -> ONE_YEAR;
          default -> throw new NoSuchElementException(String.format("Days amount %d not supported yet", days));
        };
    }
}
