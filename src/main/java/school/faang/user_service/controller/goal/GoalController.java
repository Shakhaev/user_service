package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("api/goals")
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> createGoal(@PathVariable Long userId, @RequestBody Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Цель должна иметь название.");
        }

        goalService.createGoal(userId, goal);
        return ResponseEntity.ok("Цель успешно создана!");
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<String> updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        try {
            if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
                return ResponseEntity.badRequest().body("Цель должна иметь название.");
            }

            goalService.updateGoal(goalId, goalDto);
            return ResponseEntity.ok("Цель успешно обновлена!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Произошла ошибка при обновлении цели.");
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable long goalId) {
        try {
            goalService.deleteGoal(goalId);
            return ResponseEntity.ok("Цель успешно удалена!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Цель не найдена.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Произошла ошибка при удалении цели.");
        }
    }

    @GetMapping("/subtasks/{goalId}")
    public ResponseEntity<List<GoalDto>> getSubtasksByGoalId(@PathVariable long goalId) {
        List<GoalDto> subtasks = goalService.findSubtasksByGoalId(goalId);

        if (subtasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) GoalStatus status,
            @RequestParam(required = false) Long skillId) {
        GoalFilterDto filter = new GoalFilterDto(title, status, skillId);
        List<GoalDto> goals = goalService.getGoalsByUser(userId, filter);

        if (goals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(goals);
    }
}
