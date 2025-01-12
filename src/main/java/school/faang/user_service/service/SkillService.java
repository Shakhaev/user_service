package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
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
    private final SkillValidator skillValidator;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;

    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateSkill(skillDto);

        Skill skill = skillMapper.toEntity(skillDto);
        skill.setUsers(userService.getAllByIds(skillDto.userIds()));

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
            throw new BusinessException("У пользователя уже есть предложенный скил");
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new BusinessException("Недостаточно предложений для получения скила");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        Skill finalSkill = skillRepository
                .findUserSkill(skillId, userId)
                .orElseThrow(() -> new BusinessException("Невозможно получить скилл"));
        addGuarantees(offers, finalSkill, user);

        return skillMapper.toDto(finalSkill);
    }

    private void addGuarantees(List<SkillOffer> offers, Skill finalSkill, User user) {

        List<User> users = offers.stream()
                .flatMap(offer -> offer
                        .getSkill()
                        .getUsers()
                        .parallelStream())
                .toList();

        List<UserSkillGuarantee> usersSkill = users.stream()
                .map(guaranteeUser -> guaranteeRepository.save(
                        UserSkillGuarantee.builder()
                                .skill(finalSkill)
                                .user(user)
                                .guarantor(guaranteeUser)
                                .build()))
                .toList();

        usersSkill.forEach(skill -> finalSkill.getGuarantees().add(skill));
    }
}
