package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Optional;
import java.util.stream.Stream;

@Component
public class InvitedNameFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInvitedNamePattern() != null && !filters.getInvitedNamePattern().isEmpty();
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto dto) {
        return invitations.filter(invitation -> Optional.ofNullable(invitation.getInvited())
                .map(User::getUsername)
                .orElse("")
                .contains(Optional.ofNullable(dto.getInvitedNamePattern())
                        .orElse("")));
    }
}