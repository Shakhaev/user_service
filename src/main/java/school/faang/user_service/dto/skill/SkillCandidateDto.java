package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;

public record SkillCandidateDto(@NotNull SkillDto skill,
                                long offersAmount) {
}
