package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;


@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@RequestBody SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title cannot be empty");
        }
    }
    public void getUserSkills(long userId){
        skillService.getUserSkills(userId);
    }
    public List<SkillCandidateDto> getOfferedSkills(long userId){
        return skillService.getOfferedSkills(userId);
    }
    public SkillDto acquireSkillFromOffers(long skillId, long userId){
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

}
