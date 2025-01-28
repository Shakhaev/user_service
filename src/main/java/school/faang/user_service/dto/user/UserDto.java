package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull(message = "id не может быть \"null\"")
    @Min(value = 1L, message = "id не может быть 0")
    private Long id;
    @NotBlank
    @Size(max = 64, message = "Имя пользователя не может быть больше 64 символов!")
    private String username;
}
