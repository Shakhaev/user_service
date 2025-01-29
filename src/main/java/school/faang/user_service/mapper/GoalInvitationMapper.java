package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @InheritInverseConfiguration
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Mapping(target = "goal.id", source = "goalId")
    @Mapping(target = "inviter.id", source = "inviterId")
    @Mapping(target = "invited.id", source = "invitedUserId")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    List<GoalInvitationDto> toDtoList(List<GoalInvitation> goalInvitations);

    List<GoalInvitation> toEntityList(List<GoalInvitationDto> goalInvitationDtos);
}
