package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public boolean existsById(long id) {
        return repository.existsById(id);
    }

    public User findById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User with id = " + id + " doesn't exists"));
    }
}
