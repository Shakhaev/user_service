package school.faang.user_service.service.goal.filter.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationFilterNameInvited implements InvitationFilter {
    @Override
    public boolean isAcceptable(InvitationFilterDto filters) {
        return filters.invitedNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitation, InvitationFilterDto filters) {
        return goalInvitation.filter(invitation -> invitation.getInvited() != null &&
                invitation.getInvited().getUsername().contains(filters.invitedNamePattern()));
    }
}
