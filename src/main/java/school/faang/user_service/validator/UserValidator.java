package school.faang.user_service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUserId(Long userId) {
        if (userId == null || userId >= 0) {
            throw new DataValidationException("Id пользователя не может быть Null или меньше либо равно 0");
        }
    }
}