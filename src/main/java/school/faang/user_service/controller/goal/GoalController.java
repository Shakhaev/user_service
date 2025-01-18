package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/goals")
@RestController
public class GoalController {

    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @PostMapping("/create/{userId}")
    public ResponseEntity<GoalDto> createGoal(
            @PathVariable @Positive(message = "Please, provide positive user ID") Long userId,
            @RequestBody @Valid GoalDto goalDto) {

        log.info("Creating goal for user with id {} and goalDto {}", userId, goalDto);
        Goal createdGoal = goalService.createGoal(userId, goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getSkillIds());
        return ResponseEntity.status(HttpStatus.CREATED).
                body(goalMapper.toDto(createdGoal));
    }

    @PutMapping("/update/{goalId}")
    public ResponseEntity<GoalDto> updateGoal(
            @PathVariable @Positive(message = "Please, provide positive goal ID") Long goalId,
            @RequestBody @Valid GoalDto goalDto) {

        Goal updatedGoal = goalService.updateGoal(goalId, goalMapper.toEntity(goalDto), goalDto.getSkillIds());
        return ResponseEntity.status(HttpStatus.OK).
                body(goalMapper.toDto(updatedGoal));
    }

    @DeleteMapping("/delete/{goalId}")
    public ResponseEntity<String> deleteGoal(
            @PathVariable @Positive(message = "Please, provide positive goal ID") Long goalId) {

        goalService.deleteGoal(goalId);
        return ResponseEntity.ok().body(String.format("Goal with id %s has been deleted successfully !", goalId));
    }

    @PostMapping("/find-sub-goals/{parentGoalId}")
    public ResponseEntity<List<GoalDto>> findSubGoalsByParentIdWithFilter(
            @PathVariable @Positive(message = "Please, provide positive parent goal ID") Long parentGoalId,
            @RequestBody GoalFilterDto goalFilterDto) {


        List<GoalDto> goalDtoList = goalService.findSubGoalsByParentId(parentGoalId, goalFilterDto).stream()
                .map(goalMapper::toDto).toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(goalDtoList);

    }

    @PostMapping("/find-goals/{userId}")
    public ResponseEntity<List<GoalDto>> findGoalsByUserIdWithFilter(
            @PathVariable @Positive(message = "Please, provide positive user ID") Long userId,
            @RequestBody GoalFilterDto goalFilterDto) {

        List<GoalDto> goalDtoList = goalService.findSubGoalsByUserId(userId, goalFilterDto).stream()
                .map(goalMapper::toDto).toList();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goalDtoList);

    }
}
