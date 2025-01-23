package school.faang.user_service.filter.goal.data;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InviterIdFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter) {
        return invitations.filter(invitation ->
                invitation.getInviter().getId().equals(filter.getInviterId()));
    }
}