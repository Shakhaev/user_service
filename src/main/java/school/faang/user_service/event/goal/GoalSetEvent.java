package school.faang.user_service.event.goal;

import lombok.Builder;

@Builder
public record GoalSetEvent(Long userId, Long goalId) {
}
