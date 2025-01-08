package school.faang.user_service.utility.validator.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.utility.validator.ValidatorUtils;

import java.util.Collection;

@Component
@Primary
public class ValidatorUtilsImpl implements ValidatorUtils {
    public void checkNotNull(Object value, String errorMessage) {
        if (value == null) {
            throw new DataValidationException(errorMessage);
        }
    }

    @Override
    public void checkStringNotNullOrEmpty(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new DataValidationException(errorMessage);
        }
    }

    @Override
    public void checkCollectionNotNullOrEmpty(Collection<?> collection, String errorMessage) {
        if (collection == null || collection.isEmpty()) {
            throw new DataValidationException(errorMessage);
        }
    }
}
