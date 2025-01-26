package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@AllArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public ResponseSkillDto create(CreateSkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public List<ResponseSkillDto> getUserSkills(long userId) {

        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {

        return skillService.getOfferedSkills(userId);
    }

    public ResponseSkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(CreateSkillDto skill) throws DataValidationException {
        if (StringUtils.isBlank(skill.title())) {
            throw new DataValidationException("Title can't be empty or null!");
        }
    }
}
