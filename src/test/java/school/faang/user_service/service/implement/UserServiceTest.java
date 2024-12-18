package school.faang.user_service.service.implement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.events.ProfileViewEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;

    List<Long> ids = new ArrayList<>();

    @Test
    void testGetUserShouldPublishEvent() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User user2 = User.builder().id(2L).username("username2").build();
        when(userContext.getUserId()).thenReturn(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userMapper.toDto(user)).thenReturn(new UserDto(2L));
        userService.getUser(1L);
        verify(profileViewEventPublisher, times(1)).publish(any(ProfileViewEvent.class));
    }

    @Test
    public void testGetUserThrow() {
        Long id = Long.MAX_VALUE;
        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> userService.getUser(id),
                "User by ID is not found");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 3L})
    public void testGetUserNotEmptyId(Long id) {
        User user = new User();
        user.setId(id);
        UserDto userDto = new UserDto(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User user2 = User.builder().id(2L).username("username2").build();
        when(userContext.getUserId()).thenReturn(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        UserDto resultDto = userService.getUser(id);

        verify(userMapper, times(1)).toDto(user);
        assertEquals(userDto, resultDto);
    }

    @Test
    public void testGetUsersNotEmptyList() {
        ids = List.of(2L, 3L);
        List<User> users = List.of(new User(), new User());
        users.get(0).setId(2L);
        users.get(1).setId(3L);
        List<UserDto> usersDto = List.of(new UserDto(2L), new UserDto(3L));
        when(userRepository.findAllById(ids)).thenReturn(users);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(usersDto, result);
    }

    @Test
    public void testGetUsersEmptyList() {
        ids = List.of(Long.MAX_VALUE);
        when(userRepository.findAllById(ids)).thenReturn(new ArrayList<>());

        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(new ArrayList<>(), result);
    }
}