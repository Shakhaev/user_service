package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record MentorshipRequestDto (
        Long id,
        @Positive @NotNull Long requesterId,
        @Positive @NotNull Long receiverId,
        @NotBlank String description
) {
}
