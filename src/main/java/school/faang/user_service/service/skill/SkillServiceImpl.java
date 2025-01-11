package school.faang.user_service.service.skill;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    public boolean checkSkillsExist(List<Long> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Skill list is empty");
        }

        return skillRepository.findAllById(skills).size() == skills.size();
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new IllegalStateException("Not found skill by id: " + id));
    }
}
