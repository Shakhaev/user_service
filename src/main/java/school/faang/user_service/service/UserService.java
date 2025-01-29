package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User deactivateUser(long userId) {
        // deactivation
        User user = Objects.requireNonNull(userRepository.findById(userId).orElse(null));
        user.setActive(false);
        userRepository.save(user);
        return user;
    }
}
