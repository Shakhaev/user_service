package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring",  uses = SkillMapper.class)
public interface EventMapper {

    @Mapping(target = "relatedSkills", source = "relatedSkills", qualifiedByName = "relatedSkillsToObject")
    @Mapping(target = "startDate", source = "startTime")
    @Mapping(target = "endDate", source = "endTime")
    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "status", source = "eventStatus")

    Event toEntity(EventDto eventDto);
    @Mapping(target = "relatedSkills", source = "relatedSkills", qualifiedByName = "relatedSkillsToDto")
    @Mapping(target = "startTime", source = "startDate")
    @Mapping(target = "endTime", source = "endDate")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    EventDto toDto(Event event);
}
