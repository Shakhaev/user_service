package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class UserValidator {

    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new DataValidationException("Id пользователя не может быть Null или меньше либо равно 0");
        }
    }
}