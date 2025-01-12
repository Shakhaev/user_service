package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
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

    public static int getMIN_SKILL_OFFERS() {
        return  MIN_SKILL_OFFERS ;
    }

    public Skill create(Skill skill) {
        validateSkill(skill);

        return skillRepository.save(skill);
    }

    public List<Skill> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills;
    }

    public List<Skill> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        return skills;
    }

    public Skill acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getSkillById(skillId);
        User user = getUserById(userId);

        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        existingSkillIsPresent(existingSkill, skillId, userId);

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        validateSkillOffers(skillOffers, skillId, userId);

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

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("User with ID %d not found", userId)));
    }

    private void validateSkill(Skill skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty !");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(" Skill with title already exists!");
        }
    }

    private void validateSkillOffers(List<SkillOffer> skillOffers, long skillId, long userId) {
        if (skillOffers.size() < MIN_SKILL_OFFERS) {

            throw new BusinessException(
                    String.format(" %s skill not assigned, %s is needed instead of %s",
                            getSkillById(skillId).getTitle(), MIN_SKILL_OFFERS, skillOffers.size())
            );
        }
    }

    private void existingSkillIsPresent(Optional<Skill> existingSkill, long skillId, long userId) {
        if (existingSkill.isPresent()) {

            throw new BusinessException(String.format("The assignment of the skill was rejected because the skill %s " +
                    " already exists in the user %s", getSkillById(skillId).getTitle(), getUserById(userId).getUsername()));
        }
    }

    private List<UserSkillGuarantee> userSkillGuaranteesSet(List<SkillOffer> skillOffers, long skillId, long userId) {

        List<UserSkillGuarantee> userSkillGuarantees = skillOffers
                .stream()
                .map(skillOffer -> {
                    User guarantorUser = skillOffer.getRecommendation().getAuthor();
                    return new UserSkillGuarantee(null, getUserById(userId), getSkillById(skillId), guarantorUser);
                })
                .toList();
        getSkillById(skillId).setGuarantees(userSkillGuarantees);
        return userSkillGuarantees;
    }
}
