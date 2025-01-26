package school.faang.user_service.service.goal.filter.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvitationFilterIdInviter implements InvitationFilter {

    @Override
    public boolean isAcceptable(InvitationFilterDto filters) {
        return filters.inviterId() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitation, InvitationFilterDto filters) {
        return goalInvitation.stream().filter(invitation -> invitation.getInviter() != null
                && invitation.getInviter().getId() != null
                && invitation.getInviter().getId().equals(filters.inviterId())).collect(Collectors.toList());
    }
}
