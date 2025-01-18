package school.faang.user_service.filters.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationInviterIdFilter implements GoalInvitationFilter{
    @Override
    public boolean isApplicable(GoalInvitationFilterDto filterDto) {
        return filterDto.getInviterId() != null;
    }

    @Override
    public boolean filterEntity(GoalInvitation invitation, GoalInvitationFilterDto filters) {
        return invitation.getInviter().getId().equals(filters.getInviterId());
    }
}
