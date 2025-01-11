package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findByIds(Set<Long> id) {
        return userRepository.findAllById(id);
    }
}
