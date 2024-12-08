package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserProfileRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSettingsValidator {
    private final UserProfileRepository repository;

    public void validateUserProfileByUserId(long userId) {
        if (!repository.existsById(userId)) {
            throw new EntityNotFoundException("User profile not found with id: " + userId);
        }
        log.info("User profile with userId '{}' exists.", userId);
    }
}
