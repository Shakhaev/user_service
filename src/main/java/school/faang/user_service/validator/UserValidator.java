package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator {
    public boolean checkUserExists(Optional<User> user) {
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return true;
    }
}
