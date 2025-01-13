package school.faang.user_service.utility.validator;

import school.faang.user_service.exception.DataValidationException;

import java.util.Collection;

public abstract class AbstractDataValidator<T> {

    public abstract void validate(T data);

    public void checkNotNull(Object value, String errorMessage) {
        if (value == null) {
            throw new DataValidationException(errorMessage);
        }
    }

    public void checkStringNotNullOrEmpty(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new DataValidationException(errorMessage);
        }
    }

    public void checkCollectionNotNullOrEmpty(Collection<?> collection, String errorMessage) {
        if (collection == null || collection.isEmpty()) {
            throw new DataValidationException(errorMessage);
        }
    }
}
