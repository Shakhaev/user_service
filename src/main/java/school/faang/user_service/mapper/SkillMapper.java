package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(source = "users", target = "userIds", qualifiedByName = "mapToIds")
    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);

    @Named("mapToIds")
    default List<Long> mapToIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }
}
