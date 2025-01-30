package school.faang.user_service.service.goal;

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.data.GoalDataFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.operations.GoalAssignmentHelper;
import school.faang.user_service.service.goal.operations.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private GoalAssignmentHelper goalAssignmentHelper;
    @Mock
    private GoalDataFilter goalDataFilter1;
    @Mock
    private GoalDataFilter goalDataFilter2;
    @Mock
    private GoalDataFilter goalDataFilter3;

    @Spy
    private GoalMapper goalMapper;

    private Long userId;
    private Long goalId;
    private GoalDto goalDto;
    private GoalFilterDto goalFilterDto;
    private Goal goal;
    private CreateGoalResponse createGoalResponse;
    private Goal existingGoal;
    private List<GoalDataFilter> goalDataFilters;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setup() {
        userId = 1L;
        goalId = 1L;

        goalDto = new GoalDto();
        goalDto.setId(1L);
        goalDto.setTitle("1L");
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setDescription("1L");
        goalDto.setParentId(1L);
        goalDto.setSkillIds(List.of(1L, 2L, 3L));
        goalDto.setDeadline(LocalDateTime.now().plusDays(5));

        goalFilterDto = new GoalFilterDto();
        goalFilterDto.setTitle("1L");
        goalFilterDto.setStatus(GoalStatus.ACTIVE);
        goalFilterDto.setParentId(goalId);

        goal = new Goal();
        goal.setId(1L);
        goal.setTitle(goalDto.getTitle());
        goal.setDescription(goalDto.getDescription());
        goal.setStatus(goalDto.getStatus());
        goal.setSkillsToAchieve(new ArrayList<>());
        goal.setParent(new Goal());
        goal.getParent().setId(goalDto.getParentId());

        createGoalResponse = new CreateGoalResponse();
        createGoalResponse.setId(goal.getId());
        createGoalResponse.setTitle(goal.getTitle());
        createGoalResponse.setStatus(goal.getStatus());
        createGoalResponse.setDescription(goal.getDescription());
        createGoalResponse.setParentId(goalDto.getParentId());
        createGoalResponse.setSkillIds(goalDto.getSkillIds());
        createGoalResponse.setCreatedAt(LocalDateTime.now());

        existingGoal = new Goal();
        existingGoal.setId(1L);
        existingGoal.setTitle("1L");
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setDescription("1L");
        existingGoal.setSkillsToAchieve(new ArrayList<>());

        goalDataFilters = List.of(goalDataFilter1, goalDataFilter2, goalDataFilter3);
        ReflectionTestUtils.setField(goalService, "goalDataFilters", goalDataFilters);
    }

    @Test
    void testCreateGoal_ValidatesActiveGoalsLimit() {
        goalService.createGoal(userId, goalDto);

        verify(goalValidator).validateActiveGoalsLimit(userId);
    }

    @Test
    void testCreateGoal_ValidatesSkillsExist() {
        goalService.createGoal(userId, goalDto);

        verify(goalValidator).validateSkillsExist(goalDto.getSkillIds());
    }

    @Test
    void testCreateGoal_MapsGoalDtoToEntity() {
        goalService.createGoal(userId, goalDto);

        verify(goalMapper).toEntity(goalDto);
    }

    @Test
    void testCreateGoal_SavesGoalInRepository() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);

        goalService.createGoal(userId, goalDto);

        verify(goalRepository).save(goal);
    }

    @Test
    void testCreateGoal_BindsSkillsAndMapsToResponse() {
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalMapper.toCreateResponse(any(Goal.class))).thenReturn(createGoalResponse);

        CreateGoalResponse actualResponse = goalService.createGoal(userId, goalDto);

        verify(goalAssignmentHelper).bindSkillsToGoal(goalDto.getSkillIds(), goal);
        verify(goalMapper).toCreateResponse(goal);
        Assertions.assertEquals(createGoalResponse, actualResponse);
    }


    @Test
    void testUpdateGoal_ShouldCallValidators() {
        when(goalValidator.findGoalById(goalId)).thenReturn(existingGoal);

        goalService.updateGoal(goalId, goalDto);

        verify(goalValidator).findGoalById(goalId);
        verify(goalValidator).validateGoalUpdatable(existingGoal);
        verify(goalValidator).validateSkillsExist(goalDto.getSkillIds());
    }

    @Test
    void testUpdateGoal_ShouldUpdateGoalFields() {
        when(goalValidator.findGoalById(goalId)).thenReturn(existingGoal);

        goalService.updateGoal(goalId, goalDto);

        Assertions.assertEquals(goalDto.getTitle(), existingGoal.getTitle());
        Assertions.assertEquals(goalDto.getDescription(), existingGoal.getDescription());
        Assertions.assertEquals(goalDto.getStatus(), existingGoal.getStatus());

        verify(goalAssignmentHelper).bindSkillsToGoal(goalDto.getSkillIds(), existingGoal);
    }

    @Test
    void testUpdateGoal_ShouldAssignSkillsWhenCompleted() {
        goalDto.setStatus(GoalStatus.COMPLETED);
        when(goalValidator.findGoalById(goalId)).thenReturn(existingGoal);

        goalService.updateGoal(goalId, goalDto);

        verify(goalAssignmentHelper).assignSkillsToUsers(existingGoal, goalDto.getSkillIds());
    }

    @Test
    void testUpdateGoal_ShouldSaveGoal() {
        when(goalValidator.findGoalById(goalId)).thenReturn(existingGoal);

        goalService.updateGoal(goalId, goalDto);

        verify(goalRepository).save(existingGoal);
    }

    @Test
    void testDeleteGoal_ShouldCallFindGoalByIdAndDelete() {
        when(goalValidator.findGoalById(goalId)).thenReturn(existingGoal);

        goalService.deleteGoal(goalId);

        verify(goalValidator, times(1)).findGoalById(goalId);
        verify(goalRepository, times(1)).delete(existingGoal);
    }

    @Test
    void testDeleteGoal_ShouldThrowExceptionIfGoalNotFound() {
        when(goalValidator.findGoalById(goalId)).thenThrow(new DataValidationException("Goal not found"));

        Assertions.assertThrows(DataValidationException.class, () -> goalService.deleteGoal(goalId));

        verify(goalRepository, never()).delete(any());
    }

    @Test
    void testFindSubtasksByGoalId_CallsRepository() {
        goalService.findSubtasksByGoalId(goalId, goalFilterDto);

        verify(goalRepository).findByParent(goalId);
    }

    @Test
    void testFindSubtasksByGoalId_AppliesFilters() {
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));
        when(goalDataFilter1.isApplicable(goalFilterDto)).thenReturn(true);
        when(goalDataFilter2.isApplicable(goalFilterDto)).thenReturn(false);
        when(goalDataFilter3.isApplicable(goalFilterDto)).thenReturn(true);
        when(goalDataFilter1.apply(any(), eq(goalFilterDto))).thenReturn(Stream.of(goal));
        when(goalDataFilter3.apply(any(), eq(goalFilterDto))).thenReturn(Stream.of(goal));

        goalService.findSubtasksByGoalId(goalId, goalFilterDto);

        verify(goalDataFilter1).isApplicable(goalFilterDto);
        verify(goalDataFilter3).isApplicable(goalFilterDto);
        verify(goalDataFilter1).apply(any(), eq(goalFilterDto));
        verify(goalDataFilter3).apply(any(), eq(goalFilterDto));
        verify(goalDataFilter2, never()).apply(any(), eq(goalFilterDto));
    }

    @Test
    void testFindSubtasksByGoalId_MapsToGoalDto() {
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));
        when(goalMapper.toDto(goal)).thenReturn(goalDto);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, goalFilterDto);

        verify(goalMapper).toDto(goal);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(goalDto, result.get(0));
    }

    @Test
    void testGetGoalsByUser_CallsRepository() {
        goalService.getGoalsByUser(userId, goalFilterDto);

        verify(goalRepository).findGoalsByUserId(userId);
    }

    @Test
    void testGetGoalsByUser_AppliesFilters() {
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(goalDataFilter1.isApplicable(goalFilterDto)).thenReturn(true);
        when(goalDataFilter2.isApplicable(goalFilterDto)).thenReturn(false);
        when(goalDataFilter3.isApplicable(goalFilterDto)).thenReturn(true);
        when(goalDataFilter1.apply(any(), eq(goalFilterDto))).thenReturn(Stream.of(goal));
        when(goalDataFilter3.apply(any(), eq(goalFilterDto))).thenReturn(Stream.of(goal));

        goalService.getGoalsByUser(userId, goalFilterDto);

        verify(goalDataFilter1).isApplicable(goalFilterDto);
        verify(goalDataFilter3).isApplicable(goalFilterDto);
        verify(goalDataFilter1).apply(any(), eq(goalFilterDto));
        verify(goalDataFilter3).apply(any(), eq(goalFilterDto));
        verify(goalDataFilter2, never()).apply(any(), eq(goalFilterDto));
    }

    @Test
    void testGetGoalsByUser_MapsToGoalDto() {
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(goalMapper.toDto(goal)).thenReturn(goalDto);

        List<GoalDto> result = goalService.getGoalsByUser(userId, goalFilterDto);

        verify(goalMapper).toDto(goal);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(goalDto, result.get(0));
    }
}