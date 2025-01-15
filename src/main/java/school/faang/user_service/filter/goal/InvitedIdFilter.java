package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class InvitedIdFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInvitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto dto) {
        return invitations.filter(invitation ->
                Optional.ofNullable(invitation.getInvited())
                        .map(invited -> Objects.equals(invited.getId(), dto.getInvitedId()))
                        .orElse(false));
    }
}