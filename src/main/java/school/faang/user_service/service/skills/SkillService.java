package school.faang.user_service.service.skills;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;

    public SkillDto createSkill(SkillDto skillDto) {
        validateSkill(skillDto);

        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        SkillDto dto = skillMapper.toDto(skill);
        return dto;
    }

    private void validateSkill(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("This skill already exist");
        }
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> skillUser = skillRepository.findAllByUserId(userId);
        return skillUser.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffer(long skillId, long userId) {
        Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillId, userId);
        if (optionalSkill.isPresent()) {
            throw new DataValidationException("This skill already has user");
        }
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough offers to acquire the skill");
        }
        skillRepository.assignSkillToUser(skillId, userId);
        assignGuarantorsToSkill(skillOffers);
        return skillMapper.toDto(findSkillById(skillId));
    }

    private void assignGuarantorsToSkill(List<SkillOffer> skillOffers) {
        skillOffers.forEach(offer -> userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .guarantor(offer.getRecommendation().getAuthor())
                .build()));
    }

    private Skill findSkillById(long skillId) {
        return skillRepository.findById(skillId).orElseThrow(() -> new EntityNotFoundException("Skill not found by id " + skillId));
    }

    public void assignSkillsToUser(Long skillId, Long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    public boolean skillsExist(List<Long> ids) {
        int existing = skillRepository.countExisting(ids);
        return existing == ids.size();
    }

    public List<Skill> findByIds(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }
}
