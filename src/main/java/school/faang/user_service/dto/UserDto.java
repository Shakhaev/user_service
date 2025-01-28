package school.faang.user_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String username;
    private String email;
}
