package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void testGetPremiumUsers() {
        UserRepository mockRepo = Mockito.mock(UserRepository.class);
        UserService service = new UserService(mockRepo);

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
                .city("City1")
                .premium(premium1)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .city("City2")
                .premium(premium2)
                .build();

        when(mockRepo.findPremiumUsers()).thenReturn(Stream.of(user1, user2));

        UserFilterDto filter = new UserFilterDto();
        filter.setCity("City1");

        List<UserDto> result = service.getPremiumUsers(filter);

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
    }
}
