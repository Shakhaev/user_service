package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validador.SkillValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SkillService {

    private final SkillValidator skillValidator;
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillCandidateMapper skillCandidateMapper;

    public static int getMIN_SKILL_OFFERS() {
        return MIN_SKILL_OFFERS;
    }

    public Skill create(Skill skill) {
        skillValidator.validateSkill(skill);

        return skillRepository.save(skill);
    }
    public List<Skill> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills;
    }
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills
                .stream()
                .map(skill -> {
                    SkillCandidateDto dto =
                            skillCandidateMapper.toSkillCandidateDto(skill);
                    List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skill.getId(), userId);
                    dto.setOffersAmount(skillOffers.size());
                    return dto;
                })
                .toList();
    }
    public Skill acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getSkillById(skillId);
        User user = skillValidator.getUserById(userId);

        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        skillValidator.existingSkillIsPresent(existingSkill, skillId, userId);

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        skillValidator.validateSkillOffers(skillOffers, skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);
        log.info("The skill {} was assigned to the user {} because it was received {} from {} offers",
                skill.getTitle(), user.getUsername(), skillOffers.size(), MIN_SKILL_OFFERS);

        userSkillGuaranteesSet(skillOffers, skillId, userId);

        skillRepository.save(skill);
        log.info("Updated the list of skill guarantors {} of the user {}", skill.getTitle(), user.getUsername());
        return skill;
    }
    private Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("Skill with ID %d not found", skillId)));
    }
    private List<UserSkillGuarantee> userSkillGuaranteesSet(List<SkillOffer> skillOffers, long skillId, long userId) {

        List<UserSkillGuarantee> userSkillGuarantees = skillOffers
                .stream()
                .map(skillOffer -> {
                    User guarantorUser = skillOffer.getRecommendation().getAuthor();
                    return new UserSkillGuarantee(null, skillValidator.getUserById(userId), getSkillById(skillId), guarantorUser);
                })
                .toList();
        getSkillById(skillId).setGuarantees(userSkillGuarantees);
        return userSkillGuarantees;
    }
}
