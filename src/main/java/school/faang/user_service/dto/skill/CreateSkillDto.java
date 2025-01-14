package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link school.faang.user_service.entity.Skill}
 */
public record CreateSkillDto(@NotBlank String title) {
}