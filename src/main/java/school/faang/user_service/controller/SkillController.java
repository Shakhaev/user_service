package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillMapper skillMapper;
    private final SkillService skillService;


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@Valid @RequestBody SkillDto skill) {
        Skill entitySkill = skillMapper.toEntity(skill);
        entitySkill = skillService.create(entitySkill);
        return skillMapper.toSkillDto(entitySkill);
    }

    @GetMapping("/user")
    public List<SkillDto> getUserSkills(@RequestParam long userId) {
        List<Skill> skills = skillService.getUserSkills(userId);
        return skillMapper.mapToList(skills);
    }

    @GetMapping("/offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("/acquire")
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        Skill skill = skillService.acquireSkillFromOffers(skillId, userId);
        return skillMapper.toSkillDto(skill);
    }

}
