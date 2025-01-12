package school.faang.user_service.service.filter;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilter {
    boolean applicable(GoalInvitationFilterDto filterDto);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> entities, GoalInvitationFilterDto dto);
}
