package school.faang.user_service.filter.goalInvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationStatusFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filters) {
        RequestStatus status = filters.getStatus();
        return invitations.filter((invitation) ->
                invitation.getStatus() != null &&
                invitation.getStatus().equals(status));
    }
}
