package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.publisher.goal.GoalEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalEventPublisher goalEventPublisher;

    public void notifyAboutGoalSet(long userId, long goalId) {
        log.info("Sending notification about goal set with userId - {}, and goalId - {}", userId, goalId);
        GoalSetEvent event = GoalSetEvent.builder()
                .goalId(goalId)
                .userId(userId)
                .build();
        goalEventPublisher.publish(event);
    }
}