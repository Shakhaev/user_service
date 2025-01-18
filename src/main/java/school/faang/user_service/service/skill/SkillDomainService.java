package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SkillDomainService {
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<Skill> findByIds(List<Long> skillIds) {
        return skillRepository.findByIds(skillIds);
    }

    @Transactional
    public void assignSkillToUser(long skillId, Long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    @Transactional(readOnly = true)
    public int countExisting(List<Long> ids) {
        return skillRepository.countExisting(ids);
    }
}
