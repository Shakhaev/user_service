package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoOut;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/invitation")
    public GoalInvitationDtoOut createInvitation(@RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/accept/{id}")
    public GoalInvitationDtoOut acceptGoalInvitation(@PathVariable("id") @NotNull Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/reject/{id}")
    public GoalInvitationDtoOut rejectGoalInvitation(@PathVariable("id") @NotNull Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("/filter")
    public List<GoalInvitationDtoOut> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
