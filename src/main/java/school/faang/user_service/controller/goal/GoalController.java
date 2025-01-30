package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalResponse;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<CreateGoalResponse> createGoal(
            @RequestParam Long userId,
            @Valid @RequestBody GoalDto goalDto
    ) {
        CreateGoalResponse response = goalService.createGoal(userId, goalDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<UpdateGoalResponse> updateGoal(
            @PathVariable Long goalId,
            @Valid @RequestBody GoalDto goalDto
    ) {
        UpdateGoalResponse response = goalService.updateGoal(goalId, goalDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{goalId}/subtasks")
    public ResponseEntity<List<GoalDto>> findSubtasksByGoalId(
            @PathVariable long goalId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) GoalStatus status,
            @RequestParam(required = false) Long parentId
    ) {
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle(title);
        filter.setStatus(status);
        filter.setParentId(parentId);

        List<GoalDto> subtasks = goalService.findSubtasksByGoalId(goalId, filter);
        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalDto>> getGoalsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) GoalStatus status,
            @RequestParam(required = false) Long parentId
    ) {
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle(title);
        filter.setStatus(status);
        filter.setParentId(parentId);

        List<GoalDto> goals = goalService.getGoalsByUser(userId, filter);
        return ResponseEntity.ok(goals);
    }
}