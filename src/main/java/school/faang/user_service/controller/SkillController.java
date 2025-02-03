package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import java.util.List;


@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@Valid @RequestBody SkillDto skill) {
        return skillService.create(skill);
    }

    public void getUserSkills(long userId){
        skillService.getUserSkills(userId);
    }
    public List<SkillCandidateDto> getOfferedSkills(long userId){
        return skillService.getOfferedSkills(userId);
    }
    @PostMapping("/acquire")
    public SkillDto acquireSkillFromOffers(long skillId, long userId){
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

}
