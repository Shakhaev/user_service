package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("not found user with id " + id));
    }
}
