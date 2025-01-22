package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    @Mapping(source = "users", target = "userId", qualifiedByName = "map")
    SkillDto toSkillDto(Skill skill);

    @Mapping(target = "users", ignore = true)
    Skill toEntity(SkillDto skillDto);

    @Named("map")
    default List<Long> map(List<User> users) {

        return Optional.ofNullable(users)
                .map(usersList -> usersList.stream().map(User::getId).toList())
                .orElse(List.of());
    }

    default List<SkillDto> mapToList(List<Skill> skills) {
        return skills
                .stream()
                .map(this::toSkillDto)
                .toList();
    }
}
