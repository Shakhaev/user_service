package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortUserDto {
    private Long id;
    private String username;
    private String email;
}