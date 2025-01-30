package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.GoalCannotBeCompletedException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalService goalService;

    @Test
    void testCompleteGoalsByUser_ShouldThrowExceptionWhenGoalHasOtherParticipants() {
        Stream<Goal> goals = Stream.of(Goal.builder()
                .users(List.of(User.builder().id(1L).build(),
                        User.builder().id(2L).build()))
                .build());
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(goals);

        assertThrows(GoalCannotBeCompletedException.class, () -> goalService.completeGoalsByUser(1L));
    }

    @Test
    void testCompleteGoalsByUser_Success() {
        Goal goal = Goal.builder()
                .title("Goal 1")
                .users(List.of(User.builder().id(1L).build()))
                .build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal));

        goalService.completeGoalsByUser(1L);

        verify(goalRepository, times(1)).delete(goal);
    }
}
