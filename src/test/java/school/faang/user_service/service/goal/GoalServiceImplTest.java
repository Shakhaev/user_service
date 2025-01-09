package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.exception.data.DataNotMatchException;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.goal.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillServiceInterface;
import school.faang.user_service.validator.goal.GoalServiceValidator;
import school.faang.user_service.validator.skill.SkillServiceValidator;
import school.faang.user_service.validator.user.UserServiceValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalServiceValidator goalServiceValidator;

    @Mock
    private SkillServiceValidator skillServiceValidator;

    @Mock
    private UserServiceValidator userServiceValidator;

    @Mock
    private SkillServiceInterface skillService;

    @Spy
    private GoalMapperImpl goalMapper;

    @Spy
    private UserMapper userMapper;

    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @InjectMocks
    private GoalServiceImpl goalService;

    private GoalDto goalDto;
    private Goal goal;
    private long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        goalDto = GoalDto.builder()
                .parentId(1L)
                .title("title")
                .description("description")
                .deadline(LocalDateTime.now())
                .skillsToAchieveIds(List.of(1L, 2L))
                .build();
        goal = Goal.builder()
                .id(1L)
                .title("title")
                .description("description")
                .deadline(LocalDateTime.now())
                .skillsToAchieve(List.of(
                        Skill.builder().id(1L).build(),
                        Skill.builder().id(2L).build()))
                .build();
    }

    @Test
    void testCreateGoal_GoalWithValidParameters_Success() {
        when(goalRepository
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline()))
                .thenReturn(goal);

        GoalDto createdGoalDto = goalService.create(userId, goalDto);

        assertNotNull(createdGoalDto);
        verify(goalRepository, times(1))
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
        verify(goalServiceValidator, times(1))
                .validateActiveGoalsLimit(userId);
        verify(skillService, times(1))
                .getSKillsByIds(goalDto.getSkillsToAchieveIds());
        verify(skillServiceValidator, times(1))
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
    }

    @Test
    void testCreateGoal_GoalWithInvalidLimitOfActiveGoal_Failure() {
        doThrow(new DataNotMatchException("message", null))
                .when(goalServiceValidator)
                .validateActiveGoalsLimit(userId);

        assertThrows(DataNotMatchException.class,
                () -> goalService.create(userId, goalDto));

        verify(goalServiceValidator, times(1))
                .validateActiveGoalsLimit(userId);
        verify(skillServiceValidator, never())
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
        verify(goalRepository, never())
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
    }

    @Test
    void testCreateGoal_GoalWithNotExistsSkills_Failure() {
        doThrow(new DataValidationException("message"))
                .when(skillServiceValidator)
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());

        assertThrows(DataValidationException.class,
                () -> goalService.create(userId, goalDto));

        verify(goalServiceValidator, times(1))
                .validateActiveGoalsLimit(userId);
        verify(skillServiceValidator, times(1))
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
        verify(goalRepository, never())
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
    }

    @Test
    void testCreateGoal_GoalWithNotExistsUserId_Failure() {
        doThrow(new DataValidationException("message"))
                .when(userServiceValidator)
                .existsById(userId);

        assertThrows(DataValidationException.class,
                () -> goalService.create(userId, goalDto));

        verify(userServiceValidator, times(1))
                .existsById(userId);
        verify(goalServiceValidator, never())
                .validateActiveGoalsLimit(userId);
        verify(skillServiceValidator, never())
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
        verify(goalRepository, never())
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
    }
}