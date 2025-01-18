package school.faang.user_service.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;

    @Test
    void testValidateMessage_ValidMessage() {
        String message = "Valid message";
        assertDoesNotThrow(() -> recommendationRequestValidator.validateMessage(message));
    }

    @Test
    void testValidateMessage_NullMessage() {
        String message = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> recommendationRequestValidator.validateMessage(message));
        assertEquals("Message must not be empty or blank", exception.getMessage());
    }

    @Test
    void testValidateMessage_BlankMessage() {
        String message = "   ";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> recommendationRequestValidator.validateMessage(message));
        assertEquals("Message must not be empty or blank", exception.getMessage());
    }

    @Test
    void testValidateUserExistence_UserExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validateUserExistence(userId));
    }

    @Test
    void testValidateUserExistence_UserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> recommendationRequestValidator.validateUserExistence(userId));
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void testValidateSkillsExist_ValidSkills() {
        List<Long> skillIds = List.of(1L, 2L, 3L);
        when(skillRepository.countExisting(skillIds)).thenReturn(3);
        assertDoesNotThrow(() -> recommendationRequestValidator.validateSkillsExist(skillIds));
    }

    @Test
    void testValidateSkillsExist_NullSkills() {
        List<Long> skillIds = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> recommendationRequestValidator.validateSkillsExist(skillIds));
        assertEquals("Some provided skill IDs do not exist in request", exception.getMessage());
    }

    @Test
    void testValidateSkillsExist_EmptySkills() {
        List<Long> skillIds = List.of();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> recommendationRequestValidator.validateSkillsExist(skillIds));
        assertEquals("Some provided skill IDs do not exist in request", exception.getMessage());
    }

    @Test
    void testValidateSkillsExist_SomeSkillsDoNotExist() {
        List<Long> skillIds = List.of(1L, 2L, 3L);
        when(skillRepository.countExisting(skillIds)).thenReturn(2);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> recommendationRequestValidator.validateSkillsExist(skillIds));
        assertEquals("Some provided skill IDs do not exist in the database",
            exception.getMessage());
    }
}
