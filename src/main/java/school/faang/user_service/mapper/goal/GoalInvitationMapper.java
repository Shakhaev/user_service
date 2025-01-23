package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mappings({
            @Mapping(source = "goal.id", target = "goalId"),
            @Mapping(source = "inviter.id", target = "inviterId"),
            @Mapping(source = "invited.id", target = "invitedUserId")
    })
    GoalInvitationDto toDto(GoalInvitation entity);

    @Mappings({
            @Mapping(source = "goalId", target = "goal.id"),
            @Mapping(source = "inviterId", target = "inviter.id"),
            @Mapping(source = "invitedUserId", target = "invited.id")
    })
    GoalInvitation toEntity(GoalInvitationDto dto);
}