package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.event.RegisterParticipantDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegisterParticipantMapper {
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "id", target = "eventId")
    RegisterParticipantDto toDto(User user, Event event);
}
