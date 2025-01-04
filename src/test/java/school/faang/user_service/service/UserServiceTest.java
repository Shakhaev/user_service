package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.enums.MessageError;
import school.faang.user_service.error.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    User user;

    @BeforeEach
    public void init() {
        user = new User();
        user.setId(1L);
        user.setUsername("John");
    }

    @Test
    public void testGetUserById() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    public void testUserNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = Assertions.assertThrows(UserNotFoundException.class, () ->
                userService.getUserById(1L)
        );

        Assertions.assertEquals(MessageError.USER_NOT_FOUND_EXCEPTION.getMessage(), userNotFoundException.getMessage());
    }

    @Test
    void testCreateUser() {
        Mockito.when(userRepository.save(user)).thenReturn(user);
        userService.createUser(user);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(argumentCaptor.capture());
        User capturedUser = argumentCaptor.getValue();

        Assertions.assertEquals(user.getUsername(), capturedUser.getUsername());
        Assertions.assertEquals(user.getSkills(), capturedUser.getSkills());
    }

}
