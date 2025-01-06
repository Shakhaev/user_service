package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.skill.SkillAcquiredEvent;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.publisher.skill.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserService userService;
    private final SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill skillEntity = skillMapper.toEntity(skill);
            return skillMapper.toDto(skillRepository.save(skillEntity));
        } else {
            log.error("Имя умения занято.");
            throw new DataValidationException("Умение с таким именем уже существует.");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toDto(skills);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> candidateSkills = skillRepository.findSkillsOfferedToUser(userId);
        return skillCandidateMapper.toCandidateDto(candidateSkills);
    }

    @Transactional
    public Optional<SkillDto> acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            return Optional.empty();
        }
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (offers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            Optional<Skill> guaranteeSkill = skillRepository.findUserSkill(skillId, userId);

            for (SkillOffer skillOffer : offers) {
                UserSkillGuarantee guarantee = userService.addGuaranty(userId, skillOffer);
                guaranteeSkill.get().getGuarantees().add(guarantee);
                skillRepository.save(guaranteeSkill.get());
            }
            skillAcquiredEventPublisher.publish(new SkillAcquiredEvent(userId, skillId));
            return Optional.of(skillMapper.toDto(guaranteeSkill.get()));
        }
        return Optional.empty();
    }

    private void validateSkill(SkillDto skill) {
        if (Strings.isBlank(skill.getTitle())) {
            log.error("Пустое имя умения.");
            throw new DataValidationException("Имя умения не должно быть пустым.");
        }
    }
}
