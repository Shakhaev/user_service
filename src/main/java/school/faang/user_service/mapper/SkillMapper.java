package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SkillMapper {
    @Mapping(target = "userIds", expression = "java(mapUsersToId(skill.getUsers()))")
    @Mapping(target = "eventIds", expression = "java(mapEventsToId(skill.getEvents()))")
    SkillDto toDto(Skill skill);

    default List<Long> mapEventsToId(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .toList();
    }
    default List<Long> mapUsersToId(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }
}
