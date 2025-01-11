package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventForClientDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkillsIds", expression = "java(mapSkillsToIds(event.getRelatedSkills()))")
    EventForClientDto toForClientDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(target = "relatedSkills", expression = "java(idsToSkillsIds(eventCreateDto.getRelatedSkillsIds()))")
    Event fromCreateDtoToEntity(EventCreateDto eventCreateDto);

    @Mapping(target = "relatedSkills", expression = "java(idsToSkillsIds(eventUpdateDto.getRelatedSkillsIds()))")
    Event fromUpdateDtoToEntity(EventUpdateDto eventUpdateDto);

    //@Mapping(target = "relatedSkills", expression = "java(idsToSkillsIds(eventUpdateDto.getRelatedSkillsIds()))")
    void update(@MappingTarget Event entity, EventUpdateDto eventUpdateDto);

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
                }).toList();
    }
}
