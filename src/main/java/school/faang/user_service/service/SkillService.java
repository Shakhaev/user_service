package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillCreateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
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
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;


    public SkillDto create(SkillCreateDto skillCreateDto) {
        validateSkill(skillCreateDto);
        if (skillRepository.existsByTitle(skillCreateDto.getTitle())) {
            throw new DataValidationException(" Skill с таким названием уже существует .");
        }
        Skill skill = skillMapper.toEntity(skillCreateDto);
        skill = skillRepository.save(skill);
        return skillMapper.toSkillDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        validateSkillList(skills);
        return skills.stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
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
        Skill skill = getSkillById(skillId);
        User user = getUserById(userId);
        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        if (existingSkill.isPresent()) {
            throw new BusinessException(String.format("Присвоение умения отклонено т.к умение %s "
                    + "уже существует у пользователя %s", skill.getTitle(), user.getUsername()));
        }
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new BusinessException(
                    String.format("Недостаточно предложений для присвоения умения %s." +
                                    " Необходимо %s вместо %s",
                            skill.getTitle(), MIN_SKILL_OFFERS, skillOffers.size())
            );
        }
        log.info("Умение {} присвоено пользователю {}, так как получено {} из {} предложений",
                skill.getTitle(), user.getUsername(), skillOffers.size(), MIN_SKILL_OFFERS);
        skillRepository.assignSkillToUser(skillId, userId);

        List<UserSkillGuarantee> userSkillGuarantees = skillOffers.stream()
                .map(skillOffer -> {
                    User guarantorUser = skillOffer.getRecommendation().getAuthor();
                    return new UserSkillGuarantee(null, user, skill, guarantorUser);
                })
                .toList();

        skill.setGuarantees(userSkillGuarantees);
        log.info("Обновлен список гарантов умения {} пользователя {}", skill.getTitle()
                , user.getUsername());
        skillRepository.save(skill);

        return skillMapper.toSkillDto(skill);
    }

    private Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("Умение с ID %d не найдено", skillId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("Пользователь с ID %d не найден", userId)));
    }

    private void validateSkillList(List<?> skills) {
        if (skills.isEmpty()) {
            throw new NoSuchElementException("Умения не найдены .");
        }
    }

    private void validateSkill(SkillCreateDto skill) {
        if (skill.getTitle().isBlank()) {
            throw new IllegalArgumentException("Название не может быть null или пустым !");
        }
    }
}
