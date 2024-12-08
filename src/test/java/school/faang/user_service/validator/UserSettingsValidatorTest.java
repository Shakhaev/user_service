package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserProfileRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSettingsValidatorTest {
    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserSettingsValidator userSettingsValidator;

    @Test
    void validateUserProfileByUserIdShouldThrowEntityNotFoundExceptionWhenUserProfileDoesNotExist() {
        long userId = 1L;

        when(userProfileRepository.existsById(userId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userSettingsValidator.validateUserProfileByUserId(userId));

        assertEquals("User profile not found with id: 1", exception.getMessage());
    }
}
