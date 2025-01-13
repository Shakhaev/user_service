package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * DTO for {@link school.faang.user_service.entity.goal.Goal}
 */
@Builder
public record CreateGoalRequest(@NotBlank String title,
                                @PositiveOrZero Long parentId,
                                String description,
                                GoalStatus status,
                                LocalDateTime deadline,
                                @PositiveOrZero Long mentorId,
                                List<Long> invitationIds,
                                List<Long> userIds,
                                List<Long> skillsToAchieveIds) {
    public CreateGoalRequest {
        skillsToAchieveIds = skillsToAchieveIds != null ? skillsToAchieveIds : Collections.emptyList();
        userIds = userIds != null ? userIds : Collections.emptyList();
        invitationIds = invitationIds != null ? invitationIds : Collections.emptyList();
    }
}