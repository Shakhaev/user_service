package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void shouldThrowValidateUserIdNull() {
        Assertions.assertThrows(DataValidationException.class, () -> userValidator.validateUserId(null));
    }

    @Test
    void shouldThrowValidateUserIdBlank() {
        Assertions.assertThrows(DataValidationException.class, () -> userValidator.validateUserId(-5L));
    }
}