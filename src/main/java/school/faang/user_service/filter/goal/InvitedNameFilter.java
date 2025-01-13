package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Optional;
import java.util.stream.Stream;

public class InvitedNameFilter implements InvitationFilter{

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInvitedNamePattern() != null && !filters.getInvitedNamePattern().isEmpty();
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto dto) {
        if (Optional.ofNullable(dto.getInvitedNamePattern()).isPresent()) {
            return invitations.filter(inv -> inv.getInvited().getUsername().contains(dto.getInvitedNamePattern()));
        }
        return invitations.limit(0);

    }
}
