package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO for {@link school.faang.user_service.entity.goal.Goal}
 */
public record GoalDto(Long id,
                      Long parentId,
                      @NotBlank String title,
                      String description,
                      GoalStatus status,
                      LocalDateTime deadline,
                      Long mentorId,
                      List<Long> invitationIds,
                      List<Long> userIds,
                      List<Long> skillsToAchieveIds) {
    public GoalDto {
        skillsToAchieveIds = skillsToAchieveIds != null ? skillsToAchieveIds : Collections.emptyList();
        userIds = userIds != null ? userIds : Collections.emptyList();
        invitationIds = invitationIds != null ? invitationIds : Collections.emptyList();
    }
}