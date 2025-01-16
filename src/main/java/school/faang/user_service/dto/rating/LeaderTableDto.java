package school.faang.user_service.dto.rating;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

public record LeaderTableDto(
        @NotNull @Positive Long id,
        @NotNull String username,
        @NotNull String email,
        @NotNull @Positive int ratingPoints
) implements Serializable { }
