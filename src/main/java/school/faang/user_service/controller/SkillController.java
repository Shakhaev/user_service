package school.faang.user_service.controller;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Data
@Component
public class SkillController {
    private final SkillService skillService;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    public List<SkillDto> getUserSkills(long id) {
        return skillService.getUsersSkills(id);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
