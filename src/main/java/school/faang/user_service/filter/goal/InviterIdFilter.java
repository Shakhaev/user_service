package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class InviterIdFilter implements InvitationFilter {


    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto dto) {
        return invitations.filter(invitation ->
                Optional.ofNullable(invitation.getInviter())
                        .map(inviter -> Objects.equals(inviter.getId(), dto.getInviterId()))
                        .orElse(false));
    }
}
