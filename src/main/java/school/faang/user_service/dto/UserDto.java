package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

public record UserDto(@Positive @NotNull Long id, @NotNull String username, @NotNull String email) {
}