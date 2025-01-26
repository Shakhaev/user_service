package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoOut;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {

    GoalInvitationDtoOut createInvitation(GoalInvitationDto goalInvitationDto);

    GoalInvitationDtoOut acceptGoalInvitation(long id);

    GoalInvitationDtoOut rejectGoalInvitation(long id);

    List<GoalInvitationDtoOut> getInvitations(InvitationFilterDto filter);
}
