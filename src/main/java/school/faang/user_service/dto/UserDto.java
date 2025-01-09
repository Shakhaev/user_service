package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

public record UserDto(
        @NonNull Long id,
        @NotEmpty(message = "Имя не может быть пустым")
        @Max(value = 64, message = "Имя не должно быть длиннее 64 символов")
        String username,
        @Email String email
) {
}
