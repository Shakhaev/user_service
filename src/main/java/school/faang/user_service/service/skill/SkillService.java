package school.faang.user_service.service.skill;

import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    Skill getSkillById(Long id);
    boolean checkSkillsExist(List<Long> skills);
}
