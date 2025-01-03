package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillService {
    public static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final UserSkillGuaranteeRepository skillGuaranteeRepository;
    private final SkillCandidateMapper skillCandidateMapper;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new IllegalArgumentException("Умение с таким названием уже существует");
        }
        Skill skillEntity = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skillEntity);
        return skillMapper.toDto(savedSkill);
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
        //TODO возможно ниже не верно из-за неправильного маппинга
        return offeredSkills.stream()
                .map(skillCandidateMapper::toDto)
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        String skillTitle = getSkillById(skillId).getTitle();
        String username = getUserById(userId).getUsername();
        if (existingSkill.isPresent()) {
            log.info("Умение {} уже существует у пользователя {}", skillTitle, username);
            return null;
        }
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            log.info("Умение {} присвоено пользователю {}", skillTitle, username);
            skillRepository.assignSkillToUser(skillId,userId);
            //TODO добавить гарантий от пользователей
            Skill skill = existingSkill.get();
            return skillMapper.toDto(skill);
            //skillGuaranteeRepository.save();
        }
        return null;
    }

    private Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Умение с ID %d не найдено", skillId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с ID %d не найдено", userId)));
    }

    private <T> void validateSkillList(List<T> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new NoSuchElementException("Умения не найдены");
        }
    }
}
