package school.faang.user_service.service.goal;

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
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filters.goal.GoalFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
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
    private Goal goal;
    private Goal existingGoal;
    private Goal updatedGoal;
    private Skill skill;
    private List<Skill> oldSkills;
    private List<Skill> newSkills;
    private List<Goal> subtasks;
    private List<Goal> goals;
    private GoalFilterDto filters;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setGoals(new ArrayList<>());

        skill = new Skill();
        skill.setTitle("Skill");

        goal = new Goal();
        goal.setTitle("Title");
        goal.setSkillsToAchieve(List.of(skill));
        goal.setUsers(new ArrayList<>());

        existingGoal = new Goal();
        existingGoal.setId(1L);
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setSkillsToAchieve(new ArrayList<>());

        updatedGoal = new Goal();
        updatedGoal.setDescription("Updated description");
        updatedGoal.setDeadline(LocalDateTime.now().plusDays(10));
        updatedGoal.setStatus(GoalStatus.COMPLETED);
        updatedGoal.setUpdatedAt(LocalDateTime.now());

        oldSkills = new ArrayList<>();
        oldSkills.add(new Skill());

        newSkills = new ArrayList<>();
        newSkills.add(new Skill());

        subtasks = new ArrayList<>();
        Goal subtask1 = new Goal();
        subtask1.setId(1L);
        subtask1.setTitle("GoalSubtask_one");
        Goal subtask2 = new Goal();
        subtask2.setId(2L);
        subtask2.setTitle("GoalSubtask_two");
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        goals = new ArrayList<>();
        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Goal_one");
        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Goal_two");
        goals.add(goal1);
        goals.add(goal2);

        filters = new GoalFilterDto();
        filters.setTitle("one");
    }

    @Test
    public void testCreateGoal_UserNotFound() {
        when(userService.findUserById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.createGoal(1L, goal));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testCreateGoal_ExceedsActiveGoals() {
        when(userService.findUserById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(4);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.createGoal(1L, goal));

        assertEquals("The user's number of active goals exceeds the maximum number", exception.getMessage());
    }

    @Test
    public void testCreateGoal_NonExistentSkills() {
        when(userService.findUserById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillService.skillExistsByTitle("Skill")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.createGoal(1L, goal));

        assertEquals("The goal contains non-existent skills", exception.getMessage());
    }

    @Test
    public void testCreateGoal_Success() {
        when(userService.findUserById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillService.skillExistsByTitle("Skill")).thenReturn(true);

        goalService.createGoal(1L, goal);

        verify(goalRepository, times(1)).save(goal);
        assertEquals(GoalStatus.ACTIVE, goal.getStatus());
        assertNotNull(goal.getCreatedAt());
        assertTrue(goal.getUsers().contains(user));
        assertTrue(user.getGoals().contains(goal));
    }

    @Test
    public void testAssignSkillToGoal() {
        long skillId = 1L;
        long goalId = 2L;

        goalService.assignSkillToGoal(skillId, goalId);

        verify(skillService, times(1)).assignSkillToGoal(skillId, goalId);
    }

    @Test
    public void testUpdateGoal_GoalNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.updateGoal(1L, updatedGoal));

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testUpdateGoal_ActiveGoalWithExistingSkills() {
        existingGoal.getSkillsToAchieve().add(skill);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(skillService.skillExistsByTitle("Skill")).thenReturn(true);

        goalService.updateGoal(1L, updatedGoal);

        verify(goalRepository, times(1)).save(existingGoal);
        assertEquals("Updated description", existingGoal.getDescription());
        assertEquals(updatedGoal.getDeadline(), existingGoal.getDeadline());
        assertEquals(GoalStatus.COMPLETED, existingGoal.getStatus());
        assertNotNull(updatedGoal.getUpdatedAt());
    }

    @Test
    public void testUpdateGoal_ActiveGoalWithNonExistentSkills() {
        existingGoal.getSkillsToAchieve().add(skill);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(skillService.skillExistsByTitle("Skill")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.updateGoal(1L, updatedGoal));

        assertEquals("The goal contains non-existent skills", exception.getMessage());
    }

    @Test
    public void testUpdateGoal_InactiveGoal() {
        existingGoal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(existingGoal));
        when(skillService.findSkillsByGoalId(1L)).thenReturn(List.of(skill));
        when(userService.findAllUsers()).thenReturn(List.of(user));

        goalService.updateGoal(1L, updatedGoal);

        verify(skillService, times(1)).assignSkillToGoal(skill.getId(), user.getId());
    }

    @Test
    public void testUpdateSkillsToGoal_Success() {
        long goalId = 1L;
        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(oldSkills);

        goalService.updateSkillsToGoal(goalId, newSkills);

        verify(skillService, times(oldSkills.size())).deleteSkill(any(Skill.class));
        verify(skillService, times(newSkills.size())).assignSkillToGoal(anyLong(), eq(goalId));
    }

    @Test
    public void testUpdateSkillsToGoal_NoSkillsFound() {
        long goalId = 1L;
        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.updateSkillsToGoal(goalId, newSkills));

        assertEquals("No skills found for the goal", exception.getMessage());
    }

    @Test
    public void testUpdateSkillsToGoal_EmptyNewSkillsList() {
        long goalId = 1L;
        when(goalRepository.findSkillsByGoalId(goalId)).thenReturn(oldSkills);

        goalService.updateSkillsToGoal(goalId, new ArrayList<>());

        verify(skillService, times(oldSkills.size())).deleteSkill(any(Skill.class));
        verify(skillService, never()).assignSkillToGoal(anyLong(), eq(goalId));
    }

    @Test
    public void testDeleteGoal_GoalNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.deleteGoal(1L));

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    public void testDeleteGoal_Success() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        goalService.deleteGoal(1L);

        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    public void testFindSubtasksByGoalId_Success() {
        long parentId = 1L;
        when(goalRepository.findByParent(parentId)).thenReturn(subtasks.stream());

        GoalFilter titleFilter = mock(GoalFilter.class);
        when(titleFilter.isApplicable(filters)).thenReturn(true);
        when(titleFilter.apply(any(Stream.class), eq(filters))).thenAnswer(invocation -> {
            Stream<Goal> stream = invocation.getArgument(0);
            return stream.filter(goal -> goal.getTitle().contains(filters.getTitle()));
        });

        when(goalFilters.stream()).thenReturn(Stream.of(titleFilter));

        List<Goal> result = goalService.findSubtasksByGoalId(parentId, filters);

        assertEquals(1, result.size());
        assertEquals("GoalSubtask_one", result.get(0).getTitle());
    }

    @Test
    public void testFindSubtasksByGoalId_NoSubtasksFound() {
        long parentId = 1L;
        when(goalRepository.findByParent(parentId)).thenReturn(Stream.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.findSubtasksByGoalId(parentId, filters));

        assertEquals("No subtasks found for the parent goal", exception.getMessage());
        verify(goalRepository, times(1)).findByParent(parentId);
    }

    @Test
    public void testFilterSubtasksByGoal() {
        Stream<Goal> subtasksStream = subtasks.stream();
        when(goalFilters.stream()).thenReturn(Stream.of(
                new GoalFilter() {
                    @Override
                    public boolean isApplicable(GoalFilterDto filters) {
                        return true;
                    }

                    @Override
                    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
                        return goals.filter(goal -> goal.getId() == 1L);
                    }
                }
        ));

        List<Goal> result = goalService.filterSubtasksByGoal(subtasksStream, filters);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testGetGoalsByUserId_Success() {
        long userId = 1L;
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        GoalFilter titleFilter = mock(GoalFilter.class);
        when(titleFilter.isApplicable(filters)).thenReturn(true);
        when(titleFilter.apply(any(Stream.class), eq(filters))).thenAnswer(invocation -> {
            Stream<Goal> stream = invocation.getArgument(0);
            return stream.filter(goal -> goal.getTitle().contains(filters.getTitle()));
        });

        when(goalFilters.stream()).thenReturn(Stream.of(titleFilter));

        List<Goal> result = goalService.getGoalsByUserId(userId, filters);

        assertEquals(1, result.size());
        assertEquals("Goal_one", result.get(0).getTitle());
    }

    @Test
    public void testGetGoalsByUserId_NoGoalsFound() {
        long userId = 1L;
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.getGoalsByUserId(userId, filters));

        assertEquals("No goals found for the user", exception.getMessage());
        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

    @Test
    public void testFilterGoals() {
        Stream<Goal> goalsStream = goals.stream();
        when(goalFilters.stream()).thenReturn(Stream.of(
                new GoalFilter() {
                    @Override
                    public boolean isApplicable(GoalFilterDto filters) {
                        return true;
                    }

                    @Override
                    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
                        return goals.filter(goal -> goal.getId() == 1L);
                    }
                }
        ));

        List<Goal> result = goalService.filterGoals(goalsStream, filters);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}