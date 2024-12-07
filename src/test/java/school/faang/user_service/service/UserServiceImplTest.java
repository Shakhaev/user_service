package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.country.CountryDto;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CountryMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.implementations.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryService countryService;
    @Spy
    private UserMapperImpl userMapper;
    @Spy
    private CountryMapperImpl countryMapper;

    List<Long> ids = new ArrayList<>();

    @Test
    public void testGetUserThrow() {
        long id = Long.MAX_VALUE;
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

    @Test
    public void testCreateUser() {
        String country = "Russia";
        String username = "Evgen";
        String email = "email@gmail.com";
        String password = "123123";
        UserCreateDto createDto = new UserCreateDto(username, email, password, country);
        CountryDto countryDto = new CountryDto(10L, country);
        User savedUser = User.builder().username(username).email(email).password(password)
                .active(true).country(countryMapper.toEntity(countryDto)).build();
        when(countryService.getCountryByName(createDto.country())).thenReturn(countryDto);
        when(userRepository.save(savedUser)).thenReturn(any());

        userService.createUser(createDto);

        verify(countryService, times(1)).getCountryByName(createDto.country());
        verify(userRepository, times(1)).save(savedUser);
    }
}