package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link school.faang.user_service.entity.goal.Goal}
 */
@Builder
public record GoalResponse(@PositiveOrZero Long id,
                           @PositiveOrZero Long parentId,
                           @NotBlank String title,
                           String description,
                           GoalStatus status,
                           LocalDateTime deadline,
                           @PositiveOrZero Long mentorId,
                           List<Long> invitationIds,
                           List<Long> userIds,
                           List<Long> skillsToAchieveIds) {
}