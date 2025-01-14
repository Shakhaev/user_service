package school.faang.user_service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    ONE_MONTH(30, 10),
    THREE_MONTHS(90, 25),
    ONE_YEAR(365, 80);

    private final Integer days;
    private final Integer price;

}
