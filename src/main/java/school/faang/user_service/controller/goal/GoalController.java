package school.faang.user_service.controller.goal;

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
import school.faang.user_service.dto.goal.RequestGoalDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.RequestGoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @PostMapping("/create/{user_id}")
    public ResponseEntity<Void> createGoal(
            @PathVariable("user_id") final Long userId,
            @RequestBody RequestGoalDto goalDto) {

        if (goalDto == null) {
            throw new IllegalArgumentException("RequestGoalDto is null");
        }

        if (goalDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("The Goal hasn't a title");
        }

        Goal goal = goalMapper.toEntity(goalDto);
        goalService.createGoal(userId, goal);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{goal_id}")
    public ResponseEntity<Void> updateGoal(
            @PathVariable("goal_id") final Long goalId,
            @RequestBody RequestGoalDto goalDto) {

        if (goalDto == null) {
            throw new IllegalArgumentException("RequestGoalDto is null");
        }

        if (goalDto.getTitle().isBlank()) {
            throw new IllegalArgumentException("The Goal hasn't a title");
        }

        Goal goal = goalMapper.toEntity(goalDto);
        goalService.updateGoal(goalId, goal);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{goal_id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable("goal_id") final Long goalId) {

        try {
            goalService.deleteGoal(goalId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{parent_id}/filters")
    public ResponseEntity<List<ResponseGoalDto>> findSubtasksByGoalId(
            @PathVariable("parent_id") final Long parentId,
            @RequestBody GoalFilterDto filters) {

        final List<Goal> filteredSubtasksByGoal = goalService.findSubtasksByGoalId(parentId, filters);
        List<ResponseGoalDto> filteredSubtasksByGoalDto = goalMapper.toDto(filteredSubtasksByGoal);

        return new ResponseEntity<>(filteredSubtasksByGoalDto, HttpStatus.OK);
    }


    @PostMapping("/{user_id}/filters")
    public ResponseEntity<List<ResponseGoalDto>> getGoalsByUser(
            @PathVariable("user_id") final Long userId,
            @RequestBody GoalFilterDto filters) {

        final List<Goal> filteredGoals = goalService.getGoalsByUserId(userId, filters);
        List<ResponseGoalDto> filteredGoalsDto = goalMapper.toDto(filteredGoals);

        return new ResponseEntity<>(filteredGoalsDto, HttpStatus.OK);
    }
}