package school.faang.user_service.utility.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.utility.validator.ValidatorUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EventValidatorUtilsImpl implements EventValidatorUtils {
    private final ValidatorUtils validatorUtils;

    @Override
    public void checkFutureDate(LocalDateTime date, String errorMessage) {
        if (date.isBefore(LocalDateTime.now())) {
            throw new DataValidationException(errorMessage);
        }
    }

    @Override
    public void checkChronology(LocalDateTime startDate, LocalDateTime endDate, String errorMessage) {
        if (endDate.isBefore(startDate)) {
            throw new DataValidationException(errorMessage);
        }
    }

    @Override
    public <T extends Enum<T>> void checkEnumValue(Enum<?> value, Enum<?>[] validValues, String errorMessage) {
        if (Arrays.stream(validValues).noneMatch(validValue -> Objects.equals(validValue, value))) {
            throw new DataValidationException(errorMessage);
        }
    }

    @Override
    public void checkNotNull(Object value, String errorMessage) {
        validatorUtils.checkNotNull(value, errorMessage);
    }

    @Override
    public void checkStringNotNullOrEmpty(String value, String errorMessage) {
        validatorUtils.checkStringNotNullOrEmpty(value, errorMessage);
    }

    @Override
    public void checkCollectionNotNullOrEmpty(Collection<?> collection, String errorMessage) {
        validatorUtils.checkCollectionNotNullOrEmpty(collection, errorMessage);
    }
}
