package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    public final UserRepository userRepository;

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
    }
}
