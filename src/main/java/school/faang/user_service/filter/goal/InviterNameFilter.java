package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Optional;
import java.util.stream.Stream;

public class InviterNameFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInviterNamePattern() != null && !filters.getInviterNamePattern().isEmpty();
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto dto) {
        return invitations.filter(invitation -> Optional.ofNullable(invitation.getInviter())
                .map(User::getUsername)
                .orElse("")
                .contains(Optional.ofNullable(dto.getInviterNamePattern())
                        .orElse("")));
    }
}
