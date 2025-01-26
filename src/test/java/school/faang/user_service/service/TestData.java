package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class TestData {
    public static UserDto generateUserDto() {
        return new UserDto("test username", "about test user", 20);
    }

    public static User createTestUser() {
        return new User(
                1L, "username", "email", "phone", "password", true, "aboutMe", null, "city", 20,
                LocalDateTime.now(), LocalDateTime.now(), List.of(), List.of(), List.of(), List.of(),
                List.of(new User()), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null
        );
    }
}
