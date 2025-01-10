package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void isUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            String message = "Пользователя с id " + userId + " не существует";
            log.warn(message);
            throw new BusinessException(message);
        }
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
