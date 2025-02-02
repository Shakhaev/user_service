package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserReadDto {
    Long id;
    String username;
    String email;
}