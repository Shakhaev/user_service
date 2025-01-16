package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.ACTIVE);
    }

    @Test
    void testDeactivateGoalsByUserWithOneParticipant() {
        goal.setUsers(new ArrayList<>());
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(List.of(goal).stream());

        goalService.deactivateGoalsByUser(1L);

        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    void testDeactivateGoalsByUserWithGoalNotActive() {
        goal.setStatus(GoalStatus.COMPLETED);
        goal.setUsers(new ArrayList<>());
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(List.of(goal).stream());

        goalService.deactivateGoalsByUser(1L);

        verify(goalRepository, times(0)).save(goal);
    }

    @Test
    void testDeactivateGoalsByUserWithException() {
        Long userId = 1L;
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUsers(List.of(new User(), new User()));

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));

        Exception exception = assertThrows(BusinessException.class, () -> goalService.deactivateGoalsByUser(userId));
        assertEquals("Цель имеет других участников и не может быть завершена.", exception.getMessage());
    }


    @Test
    void testDeactivateGoalsByUserNoGoals() {
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(Stream.empty());

        goalService.deactivateGoalsByUser(1L);
        verify(goalRepository, times(0)).delete(any());
        verify(goalRepository, times(0)).save(any());
    }
}
