package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Plan {
    MONTH("month"), QUARTER("quarter"), YEAR("year");

    private String value;

    public static Plan fromDays(int days) {
        switch (days) {
            case 30:
                return MONTH;
            case 90:
                return QUARTER;
            case 365:
                return YEAR;
            default:
                throw new IllegalArgumentException("Плана для такого кол-ва дней нету!");
        }
    }
}
