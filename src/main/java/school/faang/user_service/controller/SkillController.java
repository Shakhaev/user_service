package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    public SkillDto create(@Valid @RequestBody CreateSkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/user/{userId}/offered/")
    public List<SkillCandidateDto> getUserOfferedSkills(@PathVariable long userId) {
        return skillService.getUserOfferedSkills(userId);
    }

    @GetMapping("/user/{userId}/acquire/{skillId}/")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}