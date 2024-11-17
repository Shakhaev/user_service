package school.faang.user_service.controller.goal;

import jakarta.xml.bind.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public GoalDto createGoal(Long userId, GoalDto goalDto) throws ValidationException {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new ValidationException("Goal title cannot be empty");
        }

        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Goal title cannot be empty");
        }

        return goalService.updateGoal(goalId, goalDto);
    }

    public String deleteGoal(long goalId) {
        goalService.deleteGoal(goalId);
        return "Goal deleted succesfully";
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}
