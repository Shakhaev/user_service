package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkillsIds", expression = "java(mapSkillsToIds(event.getRelatedSkills()))")
    EventDto toDto(Event event);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    Event toEntity(EventDto eventDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    void update(@MappingTarget Event entity, EventDto dto);

    default List<Long> mapSkillsToIds(List<Skill> relatedSkills) {
        return relatedSkills == null ? Collections.emptyList() :
                relatedSkills.stream()
                        .map(Skill::getId)
                        .toList();
    }
}
