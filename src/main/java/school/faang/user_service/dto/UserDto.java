package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private final long id;
    private final String username;
    private final String email;
}
