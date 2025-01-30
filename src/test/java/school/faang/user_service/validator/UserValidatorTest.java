package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidatorTest {
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    void testCheckUserExists_ShouldThrowExceptionWhenUserDoesNotExist() {
        Optional<User> user = Optional.empty();
        assertThrows(EntityNotFoundException.class, () -> userValidator.checkUserExists(user));
    }

    @Test
    void testCheckUserExists_Success() {
        Optional<User> user = Optional.of(new User());
        assertTrue(userValidator.checkUserExists(user));
    }
}
