package school.faang.user_service.dto.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Component
public class InviterIdFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInviterId() != null;
    }

    @Override
    public List<GoalInvitation> apply(List<GoalInvitation> goalInvitations, InvitationFilterDto filters) {
        return goalInvitations.stream()
                .filter(f -> f.getInviter().getId().equals(filters.getInviterId()))
                .toList();
    }
}
