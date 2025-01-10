package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper
public interface SkillMapper {
    @Mapping(target = "userIds", expression = "java(mapUsersToId(skill.getUsers()))")
    @Mapping(target = "eventIds", expression = "java(mapEventsToId(skill.getEvents()))")
    SkillDto toDto(Skill skill);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "events", ignore = true)
    Skill toEntity(SkillDto skillDto);

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
