package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final int MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) throws DataValidationException {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill skillEntity = skillMapper.toEntity(skill);
            skillEntity = skillRepository.save(skillEntity);
            return skillMapper.toDto(skillEntity);
        } else {
            throw new DataValidationException("The skill already exists!");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skill -> skillMapper.toDto(skill))
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(x -> x, HashMap::new, Collectors.counting()))
                .entrySet()
                .stream()
                .map(skillLongEntry -> new SkillCandidateDto(skillMapper.toDto(skillLongEntry.getKey())
                        , skillLongEntry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {

        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("The user (id = " + userId + ") already has skill with id = " + skillId);
        } else {
            List<SkillOffer>  skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (skillOffers.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
            }
        }

        Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillId, userId);
        if (optionalSkill.isPresent()) {
            return skillMapper.toDto(optionalSkill.get());
        } else {
            throw new DataValidationException("The user (id = " + userId + ") not get skill with id = " + skillId);
        }

    }
}
