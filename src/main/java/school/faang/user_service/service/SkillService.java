package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository guaranteeRepository;
    private final UserService userService;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;

    private void validateSkill(SkillDto skillDto) {
        Objects.requireNonNull(skillDto);

        String title = skillDto.title();
        Objects.requireNonNull(title);

        if (title.isBlank()) {
            throw new DataValidationException("Название скила не может быть пустым");
        }

        if (skillRepository.existsByTitle(title)) {
            throw new DataValidationException("Скилл с таким названием уже существует");
        }
    }

    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);

        Skill skill = skillMapper.toEntity(skillDto);
        skill.setUsers(userService.getAllById(skillDto.userIds()));

        return skillMapper.toDto(skillRepository.save(skill));
    }

    public List<SkillDto> getUsersSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(skill -> skillCandidateMapper
                        .toDto(skill, skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId)))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);

        User user = userService.getById(userId);

        if (skill.isPresent()) {
            throw new IllegalArgumentException("У пользователя уже есть предложенный скил");
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new IllegalArgumentException("Недостаточно предложений для получения скила");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        Skill finalSkill = skillRepository
                .findUserSkill(skillId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Невозможно получить скилл"));
        addGuarantees(offers, finalSkill, user);

        return skillMapper.toDto(finalSkill);
    }

    private void addGuarantees(List<SkillOffer> offers, Skill finalSkill, User user) {

        offers.stream()
                .flatMap(offer -> offer
                        .getSkill()
                        .getUsers()
                        .parallelStream())
                .forEach(guaranteeUser ->
                        finalSkill
                                .getGuarantees()
                                .add(guaranteeRepository.save(
                                        UserSkillGuarantee.builder()
                                                .skill(finalSkill)
                                                .user(user)
                                                .guarantor(guaranteeUser)
                                                .build())
                                ));
    }
}
