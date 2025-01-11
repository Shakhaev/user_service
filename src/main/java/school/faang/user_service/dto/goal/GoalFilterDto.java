package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.goal.GoalStatus;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalFilterDto {
    private String title;
    private GoalStatus status;
}