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
        if (userId == null || userId == 0) {
            throw new UserValidationException("userId is null and userId is 0");
        }
    }

    public void validateUserIdExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserValidationException("user wasn't found");
        }
    }
}