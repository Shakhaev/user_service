package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @Autowired
    public GoalInvitationController(GoalInvitationService goalInvitationService) {
        this.goalInvitationService = goalInvitationService;
    }

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }

    public void acceptGoalInvitation(long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}