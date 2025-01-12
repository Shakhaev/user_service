package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.filter.GoalInvitationFilter;

import java.util.stream.Stream;

@Component
public class GoalInvitationInvitedNameFilter implements GoalInvitationFilter {
    @Override
    public boolean applicable(GoalInvitationFilterDto filterDto) {
        return filterDto.getInvitedNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> entities, GoalInvitationFilterDto dto) {
        return entities.filter(e -> e.getInvited().getUsername().equals(dto.getInvitedNamePattern()));
    }
}
