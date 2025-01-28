package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkillsIds", expression = "java(mapSkillsToIds(event.getRelatedSkills()))")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(target = "relatedSkills", expression = "java(idsToSkillsIds(createEventDto.getRelatedSkillsIds()))")
    Event fromCreateDtoToEntity(CreateEventDto createEventDto);

    @Mapping(target = "relatedSkills", expression = "java(idsToSkillsIds(updateEventDto.getRelatedSkillsIds()))")
    Event fromUpdateDtoToEntity(UpdateEventDto updateEventDto);

    @Mapping(target = "relatedSkills", expression = "java(idsToSkillsIds(updateEventDto.getRelatedSkillsIds()))")
    void update(@MappingTarget Event entity, UpdateEventDto updateEventDto);

    default List<Long> mapSkillsToIds(List<Skill> relatedSkills) {
        return relatedSkills == null ? new ArrayList<>() :
                relatedSkills.stream()
                        .map(Skill::getId)
                        .toList();
    }

    default List<Skill> idsToSkillsIds(List<Long> skillsIds) {
        return skillsIds == null ? new ArrayList<>() :
                skillsIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id);
                    return skill;
                }).collect(Collectors.toCollection(ArrayList::new));
    }
}
