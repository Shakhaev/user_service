package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService invitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        return invitationService.creatInvitation(invitationDto);
    }

    public void acceptGoalInvitation(long id) {
        invitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(long id) {
        invitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        return invitationService.getInvitations(filterDto);
    }
}
