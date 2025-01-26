package school.faang.user_service.service.goal.filter.invitation;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class InvitationFilterIdInvited implements InvitationFilter {

    @Override
    public boolean isAcceptable(InvitationFilterDto filters) {
        return filters.invitedId() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitation, InvitationFilterDto filters) {
        return goalInvitation.stream().filter(invitation -> invitation.getInvited() != null
                && invitation.getInvited().getId().equals(filters.invitedId())).collect(Collectors.toList());
    }
}
