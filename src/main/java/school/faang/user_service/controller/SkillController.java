package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillService skillService;
    private final SkillOfferRepository skillOfferRepository;

    public SkillDto create(@Validated @RequestBody SkillDto skill) {
        Skill entitySkill = skillMapper.toEntity(skill);
        entitySkill = skillService.create(entitySkill);
        return skillMapper.toSkillDto(entitySkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillService.getUserSkills(userId);
        return skills
                .stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillService.getOfferedSkills(userId);
        return skills
                .stream()
                .map(skill -> {
                    SkillCandidateDto dto =
                            skillCandidateMapper.toSkillCandidateDto(skill);
                    List<SkillOffer> skillOffers =
                            skillOfferRepository.findAllOffersOfSkill(skill.getId(), userId);
                    dto.setOffersAmount(skillOffers.size());
                    return dto;
                })
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillService.acquireSkillFromOffers(skillId, userId);
        return skillMapper.toSkillDto(skill);

    }

}
