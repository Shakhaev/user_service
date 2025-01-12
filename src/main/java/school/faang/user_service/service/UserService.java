package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User getUser(Long id) {
        log.info("Getting User with id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("not found user with id " + id));
        log.info("User with id {} found", id);
        return user;
    }
}
