package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    UserFilter userFilter;

    @Mock
    List<UserFilterDto> users;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetPremiumUsers() {
        User user = User.builder()
                .id(2L)
                .username("JaneSmith")
                .email("janesmith@example.com")
                .build();
        UserDto userDto = UserDto.builder().build();
        UserFilterDto userFilterDto = new UserFilterDto();
        when(userRepository.findPremiumUsers()).thenReturn((Stream.of(user)));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getPremiumUsers(userFilterDto);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userRepository, times(1)).findPremiumUsers();

    }
}