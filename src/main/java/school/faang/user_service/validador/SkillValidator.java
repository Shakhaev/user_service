package school.faang.user_service.validador;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MinSkillOffersException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SkillValidator {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private static final int MIN_SKILL_OFFERS = 3;

    public void validateSkill(Skill skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty !");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException(" Skill with title already exists!");
        }
    }

    public void validateSkillOffers(List<SkillOffer> skillOffers, long skillId, long userId) {
        if (skillOffers.size() < MIN_SKILL_OFFERS) {

            throw new MinSkillOffersException(
                    String.format(" %s skill not assigned, %s is needed instead of %s",
                            getSkillById(skillId).getTitle(), MIN_SKILL_OFFERS, skillOffers.size())
            );
        }
    }

    private Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("Skill with ID %d not found", skillId)));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("User with ID %d not found", userId)));
    }

    public void existingSkillIsPresent(Optional<Skill> existingSkill, long skillId, long userId) {
        if (existingSkill.isPresent()) {

            throw new MinSkillOffersException(String.format("The assignment of the skill was rejected because the skill %s " +
                    " already exists in the user %s", getSkillById(skillId).getTitle(), getUserById(userId).getUsername()));
        }
    }
}
