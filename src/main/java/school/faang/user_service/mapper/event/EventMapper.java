package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EventMapper {
    @Autowired
    SkillService skillService;

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "eventStatus", source = "status")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "relatedSkills", expression = "java(mapSkillIds(event.getRelatedSkills()))")
    public abstract EventDto toDto(Event event);

    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "status", source = "eventStatus")
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "relatedSkills", expression = "java(mapSkills(eventDto.getRelatedSkills()))")
    public abstract Event toEntity(EventDto eventDto);

    public List<Skill> mapSkills(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return ids.stream().map(id -> skillService.findSkillById(id)).toList();
    }

    public List<Long> mapSkillIds(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        return skills.stream().map(Skill::getId).toList();
    }
}
