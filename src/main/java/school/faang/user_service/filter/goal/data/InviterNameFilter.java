package school.faang.user_service.filter.goal.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InviterNameFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInviterNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter) {
        return invitations.filter(invitation ->
                invitation.getInviter().getUsername().contains(filter.getInviterNamePattern()));
    }
}