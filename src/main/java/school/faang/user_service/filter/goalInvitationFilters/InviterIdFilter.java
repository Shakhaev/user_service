package school.faang.user_service.filter.goalInvitationFilters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InviterIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto invitationFilterDto) {
        return invitationFilterDto.inviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> requests, InvitationFilterDto filterDto) {
        return requests.filter(request -> request.getInviter().getId().equals(filterDto.inviterId()));
    }
}
