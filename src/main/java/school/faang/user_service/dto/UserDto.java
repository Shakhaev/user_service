package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private final Long id;
    private final String username;
    private final String email;
}
