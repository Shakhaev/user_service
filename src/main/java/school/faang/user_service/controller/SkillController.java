package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.execption.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        validatePositiveId(userId, "Пользователя");
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        validatePositiveId(userId, "Пользователя");
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        validatePositiveId(skillId, "Умения");
        validatePositiveId(userId, "Пользователя");
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Название умения не может быть пустым");
        }
    }

    private void validatePositiveId(long id, String entityName) {
        if (id < 0) {
            throw new DataValidationException(String.format("ID %s должен быть больше 0", entityName));
        }
    }
}
