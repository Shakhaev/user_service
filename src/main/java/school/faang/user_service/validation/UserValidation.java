package school.faang.user_service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
public class UserValidation {

    private final UserRepository userRepository;

    @Autowired
    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUserId(Long userId) {
        if (userId == null || userId >= 0) {
            throw new UserValidationException("Id пользователя не может быть Null или меньше либо равно 0");
        }
    }
}