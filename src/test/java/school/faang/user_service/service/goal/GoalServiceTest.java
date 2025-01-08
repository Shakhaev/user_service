package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalDTO;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.BadRequestException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;
    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);
    @Mock
    private SkillService skillService;
    @Mock
    private UserService userService;
    @Spy
    private GoalTitleFilter goalTitleFilter;
    @Spy
    private GoalStatusFilter goalStatusFilter;
    private List<GoalFilter> goalFilters;
    private GoalDTO goalDTO;

    @Captor
    private ArgumentCaptor<Goal> captor;

    @BeforeEach
    void setUp() {
        goalFilters = Arrays.asList(goalTitleFilter, goalStatusFilter);
        goalDTO = new GoalDTO();
        goalDTO.setStatus("ACTIVE");
        goalDTO.setTitle("Title");
        goalDTO.setDescription("desc");
        goalDTO.setSkillToAchieveIds(List.of(1L, 2L));
    }

    @Test
    public void testGetSubGoalsWithFilters() {
        goalFilters = Arrays.asList(goalTitleFilter, goalStatusFilter);

        goalService = new GoalService(goalRepository, goalMapper, skillService, userService, goalFilters);

        GoalFilterDTO goalFilterDTO = new GoalFilterDTO();
        goalFilterDTO.setTitle("Other Title");
        goalFilterDTO.setStatus(GoalStatus.COMPLETED.name());

        Goal parent = new Goal();
        parent.setId(1L);
        parent.setTitle("Title");
        parent.setStatus(GoalStatus.ACTIVE);
        parent.setUsers(new ArrayList<>());
        parent.setSkillsToAchieve(new ArrayList<>());

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Other Title");
        goal2.setStatus(GoalStatus.COMPLETED);
        goal2.setUsers(new ArrayList<>());
        goal2.setSkillsToAchieve(new ArrayList<>());
        goal2.setParent(parent);

        List<Goal> goals = List.of(goal2);

        Mockito.when(goalRepository.findByParent(parent.getId())).thenReturn(goals.stream());

        List<GoalDTO> result = goalService.getSubGoals(parent.getId(), goalFilterDTO);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Other Title", result.get(0).getTitle());
    }


    @Test
    public void testGetGoalsByUserWithFilters() {
        goalFilters = Arrays.asList(goalTitleFilter, goalStatusFilter);

        goalService = new GoalService(goalRepository, goalMapper, skillService, userService, goalFilters);
        Long userId = 1L;

        GoalFilterDTO goalFilterDTO = new GoalFilterDTO();
        goalFilterDTO.setTitle("Title");
        goalFilterDTO.setStatus(GoalStatus.ACTIVE.name());

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Title");
        goal1.setStatus(GoalStatus.ACTIVE);
        goal1.setUsers(new ArrayList<>());
        goal1.setSkillsToAchieve(new ArrayList<>());

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Other Title");
        goal2.setStatus(GoalStatus.COMPLETED);
        goal2.setUsers(new ArrayList<>());
        goal2.setSkillsToAchieve(new ArrayList<>());

        List<Goal> goals = Arrays.asList(goal1, goal2);

        Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        List<GoalDTO> result = goalService.getGoalsByUser(userId, goalFilterDTO);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Title", result.get(0).getTitle());
    }


    @Test
    public void testDeleteSuccess() {
        Long goalId = 1L;

        Goal parentGoal = new Goal();
        parentGoal.setId(2L);
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setParent(parentGoal);
        Mockito.when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        Mockito.when(goalRepository.findByParent(goalId))
                .thenReturn(Stream.of(new Goal()));

        goalService.deleteGoal(goalId);

        Assertions.assertNull(parentGoal.getParent());

        Mockito.verify(goalRepository, Mockito.times(1)).delete(captor.capture());

        Goal deletedGoal = captor.getValue();
        Assertions.assertEquals(goalId, deletedGoal.getId());
    }

    @Test
    public void testUpdateGoalSuccess() {
        Long goalId = 1L;
        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setTitle("Updated Goal");
        goalDTO.setDescription("Updated Description");
        goalDTO.setDeadline(LocalDateTime.now().plusDays(10));
        goalDTO.setSkillToAchieveIds(List.of(1L, 2L));
        goalDTO.setStatus(GoalStatus.ACTIVE.name());

        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setUsers(new ArrayList<>());
        existingGoal.setTitle("Old Title");
        existingGoal.setDescription("Old Description");
        existingGoal.setDeadline(LocalDateTime.now().plusDays(5));
        existingGoal.setStatus(GoalStatus.ACTIVE);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setGoals(new ArrayList<>());
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setGoals(new ArrayList<>());
        existingGoal.setSkillsToAchieve(new ArrayList<>(List.of(skill1)));

        Mockito.when(skillService.skillsExist(goalDTO.getSkillToAchieveIds())).thenReturn(true);
        Mockito.when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        Mockito.when(skillService.findByIds(goalDTO.getSkillToAchieveIds()))
                .thenReturn(List.of(skill1, skill2));
        Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        goalService.updateGoal(goalId, goalDTO);


        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);
        Mockito.verify(goalRepository, Mockito.times(1)).save(captor.capture());
        Goal savedGoal = captor.getValue();

        Assertions.assertEquals("Updated Goal", savedGoal.getTitle());
        Assertions.assertEquals("Updated Description", savedGoal.getDescription());
        Assertions.assertEquals(2, savedGoal.getSkillsToAchieve().size());
        Assertions.assertEquals(GoalStatus.ACTIVE, savedGoal.getStatus());

    }

    @Test
    public void testUpdateWithGoalCompleted() {
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);
        Mockito.when(skillService.skillsExist(goalDTO.getSkillToAchieveIds())).thenReturn(true);
        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        Assertions.assertThrows(BadRequestException.class, () -> goalService.updateGoal(1L, goalDTO));
    }


    @Test
    public void testCreateWithBlankTitle() {
        goalDTO.setTitle(" ");
        Assertions.assertThrows(BadRequestException.class, () -> goalService.createGoal(1L, goalDTO));
    }

    @Test
    public void testCreateWithSkillsNotExists() {
        Mockito.when(skillService.skillsExist(goalDTO.getSkillToAchieveIds())).thenReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> goalService.createGoal(1L, goalDTO));
    }

    @Test
    public void testCreateUserHaveMoreActiveGoalsThanIsAllowed() {
        long userId = 1L;
        Mockito.when(skillService.skillsExist(goalDTO.getSkillToAchieveIds())).thenReturn(true);
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(4);
        Assertions.assertThrows(BadRequestException.class, () -> goalService.createGoal(userId, goalDTO));
    }

    @Test
    public void testCreate() {
        long userId = 1L;
        User user;
        Mockito.when(skillService.skillsExist(goalDTO.getSkillToAchieveIds())).thenReturn(true);
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(1);
        Mockito.when(userService.findById(userId)).thenReturn(user = new User());
        user.setGoals(new ArrayList<>());
        Mockito.when(skillService.findByIds(goalDTO.getSkillToAchieveIds())).thenReturn(new ArrayList<>());

        goalService.createGoal(userId, goalDTO);

        Mockito.verify(goalRepository, Mockito.times(1)).save(captor.capture());
        Goal goal = captor.getValue();
        Assertions.assertEquals(goalDTO.getTitle(), goal.getTitle());
    }


}
