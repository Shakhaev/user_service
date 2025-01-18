package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("There is no user with id = " + userId));
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
