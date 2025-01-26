package school.faang.user_service.service.goal.filter.invitation;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

public interface InvitationFilter {
    boolean isAcceptable(InvitationFilterDto invitationFilterDto);

    List<GoalInvitation> apply(List<GoalInvitation> goalInvitation, InvitationFilterDto invitationFilterDto);
}
