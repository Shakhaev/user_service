package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestGoalUpdateDto {
    private Long id;
    private Long parentId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private GoalStatus status;
    private Long mentorId;
 //   private List<Long> invitationIds;
 //   private List<Long> skillIds;
}