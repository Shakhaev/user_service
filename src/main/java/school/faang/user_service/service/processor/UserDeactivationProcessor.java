package school.faang.user_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.MentorshipService;

@Component
@RequiredArgsConstructor
public class UserDeactivationProcessor {
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    public void process(User user) {
        mentorshipService.deactivateMentor(user.getId());
        goalService.deactivateGoalsByUser(user.getId());
        eventService.deactivateEventsByUser(user.getId());
    }
}
