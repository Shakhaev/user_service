package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testUserDeactivation() {
        long userId = 1;
        User mockedUser = User.builder()
                .id(userId)
                .username("rauanzn")
                .phone("+7 777 777 77 77")
                .email("rauanzn@gmail.com")
                .active(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(
                Optional.of(mockedUser)
        );

        User user = userService.deactivateUser(userId);
        verify(userRepository, times(1)).save(mockedUser);
        assertFalse(user.isActive());
    }
}