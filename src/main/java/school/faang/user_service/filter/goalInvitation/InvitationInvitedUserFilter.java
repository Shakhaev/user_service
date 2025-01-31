package school.faang.user_service.filter.goalInvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationInvitedUserFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInvitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filters) {
        Long invitedId = filters.getInvitedId();
        return invitations.filter((invitation) ->
                invitation.getInvited() != null &&
                invitation.getInvited().getId() != null &&
                invitation.getInvited().getId().equals(invitedId));
    }
}
