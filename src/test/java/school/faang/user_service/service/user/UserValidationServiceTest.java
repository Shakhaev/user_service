package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserEmailAlreadyExistsException;
import school.faang.user_service.exception.user.UsernameAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final User USER = User.builder().email(EMAIL).username(USERNAME).build();

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private UserValidationService userValidationService;

    @Test
    void testValidateUsernameAndEmail_successful() {
        when(userDomainService.existsByEmail(EMAIL)).thenReturn(false);
        when(userDomainService.existsByUsername(USERNAME)).thenReturn(false);

        assertDoesNotThrow(() -> userValidationService.validateUsernameAndEmail(USER));
    }

    @Test
    void testValidateUsernameAndEmail_emailExists_exception() {
        when(userDomainService.existsByEmail(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userValidationService.validateUsernameAndEmail(USER))
                .isInstanceOf(UserEmailAlreadyExistsException.class)
                .hasMessageContaining(new UserEmailAlreadyExistsException(EMAIL).getMessage());
    }

    @Test
    void testValidateUsernameAndEmail_usernameExists_exception() {
        when(userDomainService.existsByEmail(EMAIL)).thenReturn(false);
        when(userDomainService.existsByUsername(USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> userValidationService.validateUsernameAndEmail(USER))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining(new UsernameAlreadyExistsException(USERNAME).getMessage());
    }
}