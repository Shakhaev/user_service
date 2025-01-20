package school.faang.user_service.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {

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
}
