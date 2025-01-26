package school.faang.user_service.service.goal.filter.invitation;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvitationFilterNameInviter implements InvitationFilter {

    @Override
    public boolean isAcceptable(InvitationFilterDto filters) {
        return filters.inviterNamePattern() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitation, InvitationFilterDto filters) {
        return goalInvitation.stream().filter(invitation -> invitation.getInviter() != null
                && invitation.getInviter().getUsername() != null
                && invitation.getInviter().getUsername().contains(filters.inviterNamePattern())).collect(Collectors.toList());

    }
}
