package school.faang.user_service.service.skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean areSkillsValid(List<Long> skillIds) {
        int existingSkillCount = skillRepository.countExisting(skillIds);
        return existingSkillCount == skillIds.size();
    }

}
