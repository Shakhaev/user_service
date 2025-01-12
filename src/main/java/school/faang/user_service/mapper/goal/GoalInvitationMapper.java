package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(target = "goalId", source = "goal.id")
    @Mapping(target = "inviterId", source = "inviter.id")
    @Mapping(target = "invitedUserId", source = "invited.id")
    GoalInvitationDto toDto(GoalInvitation entity);

    @Mapping(source = "inviterId", target = "inviter.id")
    @Mapping(source = "invitedUserId", target = "invited.id")
    @Mapping(source = "goalId", target = "goal.id")
    GoalInvitation toEntity(GoalInvitationDto dto);
}
