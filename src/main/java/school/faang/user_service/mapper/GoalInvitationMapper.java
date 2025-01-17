package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {
    GoalInvitation toEntity(GoalInvitationDto dto);
}