package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalCompletedEventDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalCompletedEventPublisher publisher;

    public GoalCompletedEventDto completeGoal(Long userId, Long goalId) {
        log.info("Received a request to mark the goal with ID, {}, as complete!", goalId);
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Goal was not found in DB");
                    throw new EntityNotFoundException("Goal not found");
                });
        if (goal.getStatus() != GoalStatus.COMPLETED) {
            goal.setStatus(GoalStatus.COMPLETED);
            goalRepository.save(goal);
            log.info("Mark the goal as completed");
        }
        GoalCompletedEventDto event = new GoalCompletedEventDto(
                userId, goalId, LocalDateTime.now()
        );
        publisher.publish(event);
        return event;
    }
}
