package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> users;

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

        assertThrows(EntityNotFoundException.class,
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

    @Test
    void testGetPremiumUsers() {
        User user = new User();
        UserReadDto userDto = new UserReadDto(1L, "John", "john@example.com");
        UserFilterDto userFilterDto = new UserFilterDto();
        when(userRepository.findPremiumUsers()).thenReturn((Stream.of(user)));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserReadDto> result = userService.getPremiumUsers(userFilterDto);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userRepository, times(1)).findPremiumUsers();
    }
}