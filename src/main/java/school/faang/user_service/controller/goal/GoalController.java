package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalRequest;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponse;
import school.faang.user_service.dto.goal.UpdateGoalRequest;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/create/{userId}")
    public GoalResponse createGoal(@Valid @RequestBody CreateGoalRequest goal,
                                   @PathVariable long userId) {
        return goalService.createGoal(userId, goal);
    }

    @PutMapping()
    public GoalResponse updateGoal(@Valid @RequestBody UpdateGoalRequest goal) {
        return goalService.updateGoal(goal);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{goalId}/subtasks/")
    public List<GoalResponse> getSubtasksGoal(@PathVariable long goalId) {
        return goalService.getSubtasksGoal(goalId);
    }

    @GetMapping("/{userId}")
    public List<GoalResponse> getGoals(@PathVariable long userId,
                                       @ModelAttribute GoalFilterDto filterDto) {
        return goalService.getGoals(userId, filterDto);
    }
}