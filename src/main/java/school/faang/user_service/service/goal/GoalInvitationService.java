package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDtoResponse;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {

    GoalInvitationDtoResponse createInvitation(GoalInvitationDto goalInvitationDto);

    GoalInvitationDtoResponse acceptGoalInvitation(long id);

    GoalInvitationDtoResponse rejectGoalInvitation(long id);

    List<GoalInvitationDtoResponse> getInvitations(InvitationFilterDto filter);
}
