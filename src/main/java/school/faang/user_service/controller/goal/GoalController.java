package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.RequestGoalDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/goal")
@RestController
public class GoalController {
    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @PostMapping("/create/{user-id}")
    public ResponseEntity<Void> createGoal(
            @PathVariable("user-id") final Long userId,
            @RequestBody @Valid RequestGoalDto goalDto) {

        Goal goal = goalMapper.toEntity(goalDto);
        goalService.createGoal(userId, goal);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{goal-id}")
    public ResponseEntity<Void> updateGoal(
            @PathVariable("goal-id") final Long goalId,
            @RequestBody @Valid RequestGoalDto goalDto) {

        Goal goal = goalMapper.toEntity(goalDto);
        goalService.updateGoal(goalId, goal);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{goal-id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable("goal-id") final Long goalId) {

        goalService.deleteGoal(goalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/subtasks/{parent-id}/filters")
    public ResponseEntity<List<ResponseGoalDto>> findSubtasksByGoalId(
            @PathVariable("parent-id") final Long parentId,
            @RequestBody @Valid GoalFilterDto filters) {

        List<Goal> filteredSubtasksByGoal = goalService.findSubtasksByGoalId(parentId, filters);
        List<ResponseGoalDto> filteredSubtasksByGoalDto = goalMapper.toDto(filteredSubtasksByGoal);

        return new ResponseEntity<>(filteredSubtasksByGoalDto, HttpStatus.OK);
    }

    @PostMapping("/goals/{user-id}/filters")
    public ResponseEntity<List<ResponseGoalDto>> getGoalsByUser(
            @PathVariable("user-id") final Long userId,
            @RequestBody @Valid GoalFilterDto filters) {

        final List<Goal> filteredGoals = goalService.getGoalsByUserId(userId, filters);
        List<ResponseGoalDto> filteredGoalsDto = goalMapper.toDto(filteredGoals);

        return new ResponseEntity<>(filteredGoalsDto, HttpStatus.OK);
    }
}