package school.faang.user_service.utility.validator.event.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.utility.validator.ValidatorUtils;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class EventFilterValidatorUtilsImpl implements EventFilterValidatorUtils {
    private final ValidatorUtils validatorUtils;

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
