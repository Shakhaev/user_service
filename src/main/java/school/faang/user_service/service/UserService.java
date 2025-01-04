package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.enums.MessageError;
import school.faang.user_service.error.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));
    }

    public void createUser(User user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setSkills(user.getSkills());
        userRepository.save(newUser);
    }

}
