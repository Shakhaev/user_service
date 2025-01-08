package school.faang.user_service.utility.validator.event;

import school.faang.user_service.utility.validator.ValidatorUtils;

import java.time.LocalDateTime;

public interface EventValidatorUtils extends ValidatorUtils {
    void checkFutureDate(LocalDateTime date, String errorMessage);

    void checkChronology(LocalDateTime startDate, LocalDateTime endDate, String errorMessage);

    <T extends Enum<T>> void checkEnumValue(Enum<?> value, Enum<?>[] validValues, String errorMessage);
}
