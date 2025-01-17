package school.faang.user_service.mapper.event;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillMapper {

    @Named("relatedSkillsToDto")
    public List<Long> relatedSkillsToDto(List<Skill> relatedSkills) {
        return relatedSkills.stream()
                .map(Skill::getId)
                .toList();
    }
}
