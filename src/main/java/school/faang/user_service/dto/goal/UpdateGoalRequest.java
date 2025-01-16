package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

/**
 * DTO for {@link school.faang.user_service.entity.goal.Goal}
 */
@Builder
public record UpdateGoalRequest(@NotNull @PositiveOrZero Long id,
                                @PositiveOrZero Long parentId,
                                @NotBlank String title,
                                String description,
                                GoalStatus status,
                                LocalDateTime deadline,
                                @PositiveOrZero Long mentorId) {
}