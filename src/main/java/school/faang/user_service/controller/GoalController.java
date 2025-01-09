package school.faang.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.service.GoalService;

@RestController
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/create/{userId}")
    public GoalDto createGoal(@Valid @RequestBody GoalDto goal, @PathVariable long userId) {
        return goalService.createGoal(userId, goal);
    }

    @PutMapping("/update")
    public GoalDto updateGoal(@Valid @RequestBody GoalDto goal) {
        return goalService.updateGoal(goal);
    }
}

