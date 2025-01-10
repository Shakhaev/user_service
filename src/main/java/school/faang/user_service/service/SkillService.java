package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public boolean checkSkillsExist(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Skill list is empty");
        }

        return skillRepository.findAllById(skills.stream().map(Skill::getId).toList()).size() == skills.size();
    }
}
