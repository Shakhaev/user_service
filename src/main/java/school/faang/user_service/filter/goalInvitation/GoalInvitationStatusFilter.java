package school.faang.user_service.filter.goalInvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationStatusFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.status() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream, InvitationFilterDto filterDto) {
        return goalInvitationStream.filter(
                goalInvitation -> goalInvitation.getStatus().equals(filterDto.status())
        );
    }
}
