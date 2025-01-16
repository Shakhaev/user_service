package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "eventStatus", source = "status")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "relatedSkills", expression = "java(mapSkillIds(event.getRelatedSkills()))")
    EventDto toDto(Event event);

    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "status", source = "eventStatus")
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "relatedSkills", ignore = true)
    Event toEntity(EventDto eventDto);

    default List<Long> mapSkillIds(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        return skills.stream().map(Skill::getId).toList();
    }
}
