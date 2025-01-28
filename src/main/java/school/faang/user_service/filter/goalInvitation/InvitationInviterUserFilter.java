package school.faang.user_service.filter.goalInvitation;


import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationInviterUserFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filters) {
        Long invitedId = filters.getInviterId();
        return invitations.filter((invitation) ->
                invitation.getInviter() != null &&
                invitation.getInviter().getId() != null &&
                invitation.getInviter().getId().equals(invitedId));
    }
}
