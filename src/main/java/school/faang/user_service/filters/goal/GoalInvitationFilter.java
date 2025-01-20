package school.faang.user_service.filters.goal;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.Filter;

import java.util.stream.Stream;

public interface GoalInvitationFilter extends Filter<GoalInvitation, GoalInvitationFilterDto> {
}
