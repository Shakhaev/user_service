package school.faang.user_service.mapper.event;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SkillMapper {
    private final SkillRepository skillRepository;

    @Named("relatedSkillsToObject")
    public List<Skill> relatedSkillsToObject(List<Long> relatedSkills) {
        return  relatedSkills.stream()
                .map(skillRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Named("relatedSkillsToDto")
    public List<Long> relatedSkillsToDto(List<Skill> relatedSkills) {
        return relatedSkills.stream()
                .map(Skill::getId)
                .toList();
    }
}
