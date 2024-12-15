package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalCompletedEventDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("{id}/complete")
    @ResponseStatus(HttpStatus.OK)
    public GoalCompletedEventDto completeGoal(@PathVariable Long id, @RequestParam long userId) {
        log.info("Received a request to the Goal Controller to mark the goal with ID, {}, as Completed!", id);
        return goalService.completeGoal(id, userId);
    }
}
