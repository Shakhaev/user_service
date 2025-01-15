package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;

    @NotEmpty(message = "username must not be empty")
    @NotNull(message = "username must not be null")
    private String username;

    @NotEmpty(message = "email must not be empty")
    @NotNull(message = "email must not be null")
    @Email
    private String email;
}