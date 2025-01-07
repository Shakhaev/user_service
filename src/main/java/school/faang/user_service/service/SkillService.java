package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.execption.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Умение с таким названием уже существует");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        validateSkillList(skills);
        return skills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        validateSkillList(offeredSkills);
        return offeredSkills.stream()
                .map(skill -> {
                    SkillCandidateDto dto = skillCandidateMapper.toSkillCandidateDto(skill);
                    List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skill.getId(), userId);
                    dto.setOffersAmount(skillOffers.size());
                    return dto;
                })
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getSkillById(skillId);
        User user = getUserById(userId);
        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        if (existingSkill.isPresent()) {
            log.info("Присвоение умения отклонено, так как умение {} уже существует у пользователя {}",
                    skill.getTitle(), user.getUsername());
            return null;
        }
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            log.info("Недостаточное количество предложений для присвоения умения {}. Необходимо {} вместо {}",
                    skill.getTitle(), MIN_SKILL_OFFERS, skillOffers.size());
            return null;
        }
        log.info("Умение {} присвоено пользователю {}, так как получено {} предложения из {} необходимых",
                skill.getTitle(), user.getUsername(), skillOffers.size(), MIN_SKILL_OFFERS);
        skillRepository.assignSkillToUser(skillId, userId);

        List<UserSkillGuarantee> userSkillGuarantees = skillOffers.stream()
                .map(skillOffer -> {
                    User guarantorUser = skillOffer.getRecommendation().getAuthor();
                    return new UserSkillGuarantee(null, user, skill, guarantorUser);
                })
                .toList();

        skill.setGuarantees(userSkillGuarantees);
        log.info("Обновлен список гарантов умения {} пользователя {}", skill.getTitle(), user.getUsername());
        skillRepository.save(skill);

        return skillMapper.toDto(skill);
    }

    private Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Умение с ID %d не найдено", skillId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с ID %d не найден", userId)));
    }

    private void validateSkillList(List<?> skills) {
        if (skills.isEmpty()) {
            throw new NoSuchElementException("Умения не найдены");
        }
    }

    public static int getMinSkillOffers() {
        return MIN_SKILL_OFFERS;
    }
}
