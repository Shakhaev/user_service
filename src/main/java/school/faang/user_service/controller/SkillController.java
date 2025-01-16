package school.faang.user_service.controller;

import lombok.Data;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
@Data
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) throws DataValidationException {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public void validateSkill(SkillDto skill) throws DataValidationException {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Title can't be empty or null!");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
