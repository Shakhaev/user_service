package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
