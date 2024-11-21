package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import school.faang.user_service.model.jpa.Skill;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Named("toSkillNameList")
    default List<String> toSkillNameList(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getTitle)
                .toList();
    }
}
