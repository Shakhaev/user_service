package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public void validateUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User not found with id: %d", userId));
        }
    }

    public void validateSkillsExist(List<Long> skillsIds) {
        if (skillsIds == null || skillsIds.isEmpty()) {
            throw new IllegalArgumentException(
                "Some provided skill IDs do not exist in request");
        }

        int countSkill = skillRepository.countExisting(skillsIds);
        if (countSkill != skillsIds.size()) {
            throw new IllegalArgumentException(
                "Some provided skill IDs do not exist in the database");
        }
    }
}
