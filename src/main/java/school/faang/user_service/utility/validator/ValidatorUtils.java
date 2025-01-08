package school.faang.user_service.utility.validator;

import java.util.Collection;

public interface ValidatorUtils {
    void checkNotNull(Object value, String errorMessage);

    void checkStringNotNullOrEmpty(String value, String errorMessage);

    void checkCollectionNotNullOrEmpty(Collection<?> collection, String errorMessage);
}
