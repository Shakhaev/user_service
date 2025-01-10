package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper
public interface InvitationMapper {
    InvitationMapper INSTANCE = Mappers.getMapper(InvitationMapper.class);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    GoalInvitation toEntity(Long invitationId);
}
