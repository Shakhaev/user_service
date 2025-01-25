package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.RegisterParticipantDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegisterParticipantMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "event.id", target = "eventId")
    RegisterParticipantDto toDto(User user, Event event);

    Event toEvent(RegisterParticipantDto registerParticipantDto);

    List<UserDto> toDtoList (List<User> users);
    List <Event> toEventList (List<RegisterParticipantDto> events);
}
