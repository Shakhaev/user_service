package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.publisher.goal.GoalEventPublisher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalEventPublisher publisher;
    @InjectMocks
    private GoalService goalService;

    @Test
    void notifyAboutGoalSetSuccessTest() {
        GoalSetEvent event = GoalSetEvent.builder()
                .goalId(1L)
                .userId(1L)
                .build();
        assertDoesNotThrow(() -> goalService.notifyAboutGoalSet(1L, 1L));
        verify(publisher).publish(event);
    }
}
