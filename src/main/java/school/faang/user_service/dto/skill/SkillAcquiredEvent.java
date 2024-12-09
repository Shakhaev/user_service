package school.faang.user_service.dto.skill;

import lombok.Builder;

@Builder
public record SkillAcquiredEvent(long userId, long skillId) {
}
