package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserEmailAlreadyExistsException;
import school.faang.user_service.exception.user.UsernameAlreadyExistsException;

@RequiredArgsConstructor
@Service
public class UserValidationService {
    private final UserDomainService userDomainService;

    public void validateUsernameAndEmail(User user) {
        if (userDomainService.existsByEmail(user.getEmail())) {
            throw new UserEmailAlreadyExistsException(user.getEmail());
        }
        if (userDomainService.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
    }
}
