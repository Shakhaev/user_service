package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegistrationDto(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank String password,
        @NotNull Long countryId
) {}

