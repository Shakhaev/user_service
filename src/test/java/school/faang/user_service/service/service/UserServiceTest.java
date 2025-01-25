package school.faang.user_service.service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.filters.subscription.CityFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl mapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("get-premium-user | service")
    void testGetPremiumUsers() {

        userService = new UserService(userRepository, mapper, List.of(new CityFilter()));

        Premium premium1 = Premium.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        Premium premium2 = Premium.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .city("Tashkent")
                .premium(premium1)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .city("Dubai")
                .premium(premium2)
                .build();

        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user1, user2));

        UserFilterDto filterDto = UserFilterDto.builder().cityPattern("Tashkent").build();

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        String username = result.get(0).username();

        assertEquals(1, result.size());
        assertEquals("user1", username);
    }
}
