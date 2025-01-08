package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User testUser;
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(USER_ID).build();
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        User result = userService.getUser(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getUser_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.getUser(USER_ID));

        assertEquals("not found user with id " + USER_ID, exception.getMessage());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getUser_ShouldNotCallRepositoryForNullId() {
        Long nullUserId = null;

        assertThrows(IllegalArgumentException.class, () -> userService.getUser(nullUserId));

        verify(userRepository, never()).findById(any());
    }
}