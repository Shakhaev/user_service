package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static school.faang.user_service.constant.UserErrorMessages.USER_WITH_ID_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl projectMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUser_WhenUserExists_ReturnsUserDto() {
        long userId = 1L;
        User project = createTestUser();
        UserDto expectedUserDto = projectMapper.toDto(project);

        when(userRepository.findById(userId)).thenReturn(Optional.of(project));
        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(expectedUserDto, result);

        verify(userRepository).findById(userId);
    }

    @Test
    void getUser_WhenUserDoesNotExist_ThrowsEntityNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));

        assertEquals(String.format(USER_WITH_ID_NOT_FOUND, userId), exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(projectMapper);
    }

    private User createTestUser() {
        return User.builder().id(1L).username("Test User").build();
    }

}