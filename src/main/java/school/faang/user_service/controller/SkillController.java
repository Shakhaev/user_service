package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.execption.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(@Valid @RequestBody SkillDto skill) {
        validateTitle(skill);
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateTitle(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Название умения не может быть пустым");
        }
    }

}
