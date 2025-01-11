package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "skillsIds", expression = "java(mapSkillsToIds(user.getSkills()))")
    @Mapping(target = "participatedEventsIds", expression = "java(mapEventsToIds(user.getParticipatedEvents()))")
    @Mapping(target = "ownedEventsIds", expression = "java(mapEventsToIds(user.getOwnedEvents()))")
    UserDto toDto(User user);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    default List<Long> mapEventsToIds(List<Event> skills) {
        return skills.stream()
                .map(Event::getId)
                .toList();
    }
}
