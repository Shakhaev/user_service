package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testUserExists() {
        Long userId = 1L;
        boolean expectedResult = true;

        when(userRepository.existsById(userId))
                .thenReturn(expectedResult);

        boolean actualResult = userService.userExists(userId);

        verify(userRepository, times(1))
                .existsById(eq(userId));

        assertEquals(expectedResult, actualResult);
    }
}
