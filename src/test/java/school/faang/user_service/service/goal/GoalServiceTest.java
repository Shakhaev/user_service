package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exeption.MaxActiveGoalsExceededException;
import school.faang.user_service.exeption.NoSkillsFoundException;
import school.faang.user_service.exeption.NonExistentSkillException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filters.goal.GoalFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private List<GoalFilter> goalFilters;

    @InjectMocks
    private GoalService goalService;

    private User user;
    private Goal goal1;
    private Goal goal2;
    private Goal goal3;
    private Skill skill;
    private List<Goal> goals;
    private List<Skill> skills;
    private List<Skill> skillsNew;
    private List<Goal> subtasks;
    private GoalFilterDto filters;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .id(1L)
                .title("Skill")
                .build();

        skills = new ArrayList<>();
        skills.add(skill);
        skillsNew = new ArrayList<>();
        skillsNew.add(skill);

        goal1 = Goal.builder()
                .id(1L)
                .title("Goal one")
                .description("Description one")
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(skills)
                .build();

        goal2 = Goal.builder()
                .id(2L)
                .parent(goal1)
                .title("Goal two")
                .description("Description two")
                .build();

        goal3 = Goal.builder()
                .title("Goal one")
                .description("Description updated")
                .skillsToAchieve(skills)
                .build();

        goals = new ArrayList<>();
        goals.add(goal1);
        goals.add(goal2);

        user = User.builder()
                .id(1L)
                .goals(goals)
                .build();

        subtasks = new ArrayList<>();
        subtasks.add(goal2);

        filters = new GoalFilterDto();
        filters.setTitle("two");
    }

    @Test
    public void testCreateGoal_UserNotFound() {
        when(userService.findUserById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalService.createGoal(1L, goal1));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testCreateGoal_ExceedsActiveGoals() {
        when(userService.findUserById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(4);

        MaxActiveGoalsExceededException exception = assertThrows(MaxActiveGoalsExceededException.class, () ->
                goalService.createGoal(1L, goal1));

        assertEquals("The user's number of active goals exceeds the maximum number", exception.getMessage());
    }

    @Test
    public void testCreateGoal_NonExistentSkills() {
        when(userService.findUserById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillService.skillExistsByTitle("Skill")).thenReturn(false);

        NonExistentSkillException exception = assertThrows(NonExistentSkillException.class, () ->
                goalService.createGoal(1L, goal1));

        assertEquals("The goal contains non-existent skills", exception.getMessage());
    }

    @Test
    public void testCreateGoal_Success() {
        when(userService.findUserById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillService.skillExistsByTitle("Skill")).thenReturn(true);

        goalService.createGoal(1L, goal1);

        verify(goalRepository, times(1)).save(goal1);
        assertEquals(GoalStatus.ACTIVE, goal1.getStatus());
        assertNotNull(goal1.getCreatedAt());
        assertTrue(goal1.getUsers().contains(user));
        assertTrue(user.getGoals().contains(goal1));
    }

    @Test
    public void testUpdateGoal_GoalNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalService.updateGoal(1L, goal1));

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateGoal_ActiveGoalWithExistingSkills() {
        goal1.getSkillsToAchieve().add(skill);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal1));
        when(skillService.skillExistsByTitle("Skill")).thenReturn(true);

        goalService.updateGoal(1L, goal3);

        verify(goalRepository, times(1)).save(goal1);
        assertEquals("Description updated", goal1.getDescription());
    }

    @Test
    public void testUpdateSkillsToGoal_Success() {
        long goalId = 1L;
        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(skills);

        goalService.updateSkillsToGoal(goalId, skillsNew);

        verify(skillService, times(skills.size())).deleteSkill(any(Skill.class));
        verify(skillService, times(skillsNew.size())).assignSkillToGoal(anyLong(), eq(goalId));
    }

    @Test
    public void testUpdateSkillsToGoal_NoSkillsFound() {
        long goalId = 1L;
        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(new ArrayList<>());

        NoSkillsFoundException exception = assertThrows(NoSkillsFoundException.class, () ->
                goalService.updateSkillsToGoal(goalId, skills));

        assertEquals("No skills found for the goal", exception.getMessage());
    }

    @Test
    public void testUpdateSkillsToGoal_EmptyNewSkillsList() {
        long goalId = 1L;
        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(skills);

        goalService.updateSkillsToGoal(goalId, new ArrayList<>());

        verify(skillService, times(skills.size())).deleteSkill(any(Skill.class));
        verify(skillService, never()).assignSkillToGoal(anyLong(), eq(goalId));
    }

    @Test
    public void testDeleteGoal_GoalNotFound() {
        when(goalRepository.findById(4L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalService.deleteGoal(4L));

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testDeleteGoal_Success() {
        when(goalRepository.findById(2L)).thenReturn(Optional.of(goal2));

        goalService.deleteGoal(2L);

        verify(goalRepository, times(1)).delete(goal2);
    }

    @Test
    public void testFindSubtasksByGoalId() {
        long parentId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<Goal> mockSubtasks = List.of(new Goal());
        when(goalRepository.findByParent(parentId)).thenReturn(mockSubtasks.stream());

        List<Goal> result = goalService.findSubtasksByGoalId(parentId, filters);

        assertEquals(mockSubtasks, result);
        verify(goalRepository, times(1)).findByParent(parentId);
    }

    @Test
    public void testFilterSubtasksByGoal() {
        List<Goal> subtasks = List.of(new Goal());
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter mockFilter = mock(GoalFilter.class);
        when(mockFilter.isApplicable(filters)).thenReturn(true);
        when(mockFilter.apply(any(Stream.class), eq(filters))).thenAnswer(invocation -> {
            Stream<Goal> stream = invocation.getArgument(0);
            return stream.filter(goal -> true);
        });

        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));

        List<Goal> result = goalService.filterSubtasksByGoal(subtasks, filters);

        assertEquals(subtasks, result);
        verify(mockFilter, times(1)).isApplicable(filters);
        verify(mockFilter, times(1)).apply(any(Stream.class), eq(filters));
    }

    @Test
    public void testGetGoalsByUserId() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<Goal> mockGoals = List.of(new Goal());
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(mockGoals.stream());

        List<Goal> result = goalService.getGoalsByUserId(userId, filters);

        assertEquals(mockGoals, result);
        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

    @Test
    public void testFilterGoals() {
        List<Goal> goals = List.of(new Goal());
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter mockFilter = mock(GoalFilter.class);
        when(mockFilter.isApplicable(filters)).thenReturn(true);
        when(mockFilter.apply(any(Stream.class), eq(filters))).thenAnswer(invocation -> {
            Stream<Goal> stream = invocation.getArgument(0);
            return stream.filter(goal -> true);
        });

        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));

        List<Goal> result = goalService.filterGoals(goals, filters);

        assertEquals(goals, result);
        verify(mockFilter, times(1)).isApplicable(filters);
        verify(mockFilter, times(1)).apply(any(Stream.class), eq(filters));
    }

    @Test
    public void testUpdateSkillsToGoal() {
        long goalId = 1L;

        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(skills);

        goalService.updateSkillsToGoal(goalId, skillsNew);

        verify(goalRepository, times(1)).findSkillsByGoalId(goalId);
        skills.forEach(skill -> verify(skillService, times(1)).deleteSkill(skill));
        skillsNew.forEach(skill -> verify(skillService, times(1)).assignSkillToGoal(skill.getId(), goalId));
    }

    @Test
    public void testAssignSkillToGoal() {
        long skillId = 1L;
        long goalId = 2L;

        goalService.assignSkillToGoal(skillId, goalId);

        verify(skillService, times(1)).assignSkillToGoal(skillId, goalId);
    }
}