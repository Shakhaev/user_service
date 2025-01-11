package school.faang.user_service.dto.goal.filter;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

public interface InvitationFilter {
    boolean isApplicable(InvitationFilterDto filters);

    List<GoalInvitation> apply(List<GoalInvitation> goalInvitations,
                                  InvitationFilterDto filters);
}
