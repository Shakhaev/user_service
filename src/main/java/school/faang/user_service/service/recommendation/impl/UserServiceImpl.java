package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.recommendation.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;

    @Override
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
    }
}
