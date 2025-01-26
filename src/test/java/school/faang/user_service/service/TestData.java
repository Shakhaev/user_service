package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

public class TestData {
    public static UserDto generateUserDto() {
        return new UserDto("test username", "about test user");
    }
}
