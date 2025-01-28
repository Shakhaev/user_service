package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/skill/create")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/skill/{userId}/getUsersSkill")
    public List<SkillDto> getUserSkills(@PathVariable @Min(1) long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/skill/{userId}/getOfferedSkills")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable @Min(1) long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PutMapping("/skill/{userId}/acquireSkillFromOffers")
    public SkillDto acquireSkillFromOffers(@PathVariable @Valid @Min(1) long skillId, @PathVariable @Valid @Min(1) long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}