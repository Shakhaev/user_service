package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllById(List<Long> userIds) {
        Objects.requireNonNull(userIds);
        return userRepository.findAllById(userIds);
    }

    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Невозможно получить пользователя"));
    }
}
