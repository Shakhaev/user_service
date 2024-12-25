package school.faang.user_service.filter.goalInvitationFilters;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilter {
    boolean isApplicable(InvitationFilterDto invitationFilterDto);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> request, InvitationFilterDto filterDto);
}
