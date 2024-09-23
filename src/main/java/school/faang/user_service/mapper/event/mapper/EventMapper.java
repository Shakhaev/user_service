package school.faang.user_service.mapper.event.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", ignore = true)
    EventDto toDto(Event event);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    Event toEvent(EventDto eventDto);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToDataTime")
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "localDateTimeToDataTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "localDateTimeToDataTime")
    Event toEvent(EventFilterDto eventFilterDto);

    @AfterMapping
    default void mapRelatedSkillsTargetEvent(EventDto eventDto, @MappingTarget Event event, SkillRepository skillRepository) {
        List<Skill> skills = skillRepository.findAllByUserId(eventDto.getOwnerId());
        event.setRelatedSkills(skills);
    }

    @AfterMapping
    default void mapOwnerIdInOwner(EventDto eventDto, @MappingTarget Event event, UserRepository userRepository) {
        User user = userRepository.getById(eventDto.getOwnerId());
        event.setOwner(user);
    }

    @AfterMapping
    default void mapRelatedSkillsTargetEventDto(Event event, @MappingTarget EventDto eventDto) {
        List<Long> skillsId = event.getRelatedSkills().stream()
                .map(Skill::getId)
                .toList();

        eventDto.setRelatedSkills(skillsId);
    }

    @Named("localDateTimeToDataTime")
    default LocalDateTime localDateTimeToDataTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

}