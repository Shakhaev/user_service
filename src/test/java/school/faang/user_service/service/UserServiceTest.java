package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.faang.user_service.service.TestData.generateUserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser() {
        UserDto userDto = generateUserDto();
        UserDto res = userService.createUser(userDto);
        assertEquals(userDto.username(), res.username());
        assertEquals(userDto.aboutMe(), res.aboutMe());
    }
}