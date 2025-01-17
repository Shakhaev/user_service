package school.faang.user_service.service.decorators.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.interfaces.SkillServiceI;

import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class SkillServiceDecorator implements SkillServiceI {
    protected final SkillService skillService;
    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @Override
    public List<SkillDto> getUserSkills(Long userId) {
        return skillService.getUserSkills(userId);
    }

    @Override
    public List<SkillCandidateDto> getUserOfferedSkills(Long userId) {
        return skillService.getUserOfferedSkills(userId);
    }

    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
