package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;

    public SkillDto findSkillById(Long id) {
        return skillMapper.toDto(skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found, id: " + id)));
    }


    public List<SkillDto> findSkillsByUserId(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        Skill skillToSave = skillMapper.toEntity(skill);

        return skillMapper.toDto(skillRepository.save(skillToSave));

    }
    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(skillMapper::toCandidateDto)
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        int MIN_SKILL_OFFERS = 3;
        if (skillRepository.findUserSkill(skillId, userId) != null) {
            return null;
        }
        List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
        }
        Skill skill = new Skill();
        for (SkillOffer offer : allOffersOfSkill) {
            skill.setGuarantees(offer.getSkill().getGuarantees());
        }
        skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("The skill name cannot be empty!");
        }
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title " + skillDto.getTitle() + " already exist");
        }
    }
}
