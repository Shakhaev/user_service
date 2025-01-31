package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalService goalService;

    @Test
    void testStopGoalsByUser_ShouldRemoveUserFromParticipantsList() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Stream<Goal> goals = Stream.of(Goal.builder()
                .users(new ArrayList<>(List.of(user1, user2)))
                .build());
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(goals);

        List<User> expected = new ArrayList<>(List.of(user2));

        List<User> actual = goalService.stopGoalsByUser(1L).get(0).getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void testStopGoalsByUser_Success() {
        Goal goal = Goal.builder()
                .title("Goal 1")
                .users(List.of(User.builder().id(1L).build()))
                .build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal));

        goalService.stopGoalsByUser(1L);

        verify(goalRepository, times(1)).delete(goal);
    }
}
