package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public boolean skillExistsByTitle(String title) {
        return skillRepository.existsByTitle(title);
    }

    public void assignSkillToGoal(long skillId, long goalId) {
        skillRepository.assignSkillToGoal(skillId, goalId);
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public void deleteSkill(Skill skill) {
        skillRepository.delete(skill);
    }
}