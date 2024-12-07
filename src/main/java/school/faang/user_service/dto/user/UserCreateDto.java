package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank
        @Size(min = 1, max = 64, message = "Максимальная длина имени 64 символа")
        String username,

        @NotBlank
        @Email
        @Size(min = 1, max = 64, message = "Максимальная длина email 64 символа")
        String email,

        @NotBlank
        @Size(min = 1, max = 128, message = "Максимальная длина пароля 128 символа")
        String password,

        @NotBlank
        String country
) {

}
