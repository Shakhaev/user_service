package school.faang.user_service.dto.goal;

import lombok.Data;
import org.joda.time.LocalDateTime;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
public class UpdateGoalResponse {
    private Long id;
    private String title;
    private GoalStatus status;
    private String description;
    private Long parentId;
    private List<Long> skillIds;
    private LocalDateTime updatedAt;
    private String message;
}