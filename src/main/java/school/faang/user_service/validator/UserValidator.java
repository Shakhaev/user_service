package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException.userNotFoundException(userId);
        }
    }
}
