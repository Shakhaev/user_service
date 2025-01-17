package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSuccessWhenUserExists() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.isUserExists(anyLong());
        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenUserNotExists() {
        long nonExistingUserId = 123L;
        when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> userService.isUserExists(nonExistingUserId),
                "Пользователя с id " + nonExistingUserId + " не существует");
    }

    @Test
    void shouldSuccessSaveUser() {
        User user = new User();
        userService.saveUser(user);

        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }
}