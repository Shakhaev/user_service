package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactPreferenceRepository contactPreferenceRepository;

    @InjectMocks
    private UserValidator userValidator;

    private final Long userId = 1L;

    @Test
    void validateUserByIdWrongId() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userValidator.validateUserById(1L));
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void validateUserByIdValidId() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.validateUserById(1L));
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void validateUserProfileByUserIdShouldNotThrowWhenUserExists() {
        when(contactPreferenceRepository.existsByUserId(userId)).thenReturn(true);

        userValidator.validateUserProfileByUserId(userId);

        verify(contactPreferenceRepository, times(1)).existsByUserId(userId);
    }

    @Test
    void validateUserProfileByUserIdShouldThrowWhenUserDoesNotExist() {
        when(contactPreferenceRepository.existsByUserId(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                userValidator.validateUserProfileByUserId(userId)
        );

        verify(contactPreferenceRepository, times(1)).existsByUserId(userId);
    }
}
