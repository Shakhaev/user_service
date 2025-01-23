package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
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

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public SkillDto create(CreateSkillDto skill) {
        validateSkill(skill);
        Skill skillToSave = skillMapper.toSkillEntityFromCreateDto(skill);

        return skillMapper.toDto(skillRepository.save(skillToSave));

    }

    @Transactional(readOnly = true)
    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(skillMapper::toCandidateDto)
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        int MIN_SKILL_OFFERS = 3;
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill with Id " + skillId + " not found!"));
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);

        if (userSkill.isPresent()) {
            return null;
        }
        List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (allOffersOfSkill.size() < MIN_SKILL_OFFERS) {
            throw new IllegalArgumentException("This skill has been offered less than "
                    + MIN_SKILL_OFFERS + " times.");
        }
        skillRepository.assignSkillToUser(skillId, userId);
        for (SkillOffer offer : allOffersOfSkill) {
            UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
            userSkillGuarantee.setGuarantor(offer.getRecommendation().getAuthor());
            userSkillGuarantee.setSkill(skill);
            userSkillGuarantee.setUser(offer.getRecommendation().getReceiver());
            userSkillGuaranteeRepository.save(userSkillGuarantee);
        }

        return skillMapper.toDto(skill);
    }

    private void validateSkill(CreateSkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("The skill name cannot be empty!");
        }
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title " + skillDto.getTitle() + " already exist");
        }
    }
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

}
