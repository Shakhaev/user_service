package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.data.GoalDataFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.operations.GoalAssignmentHelper;
import school.faang.user_service.service.goal.operations.GoalValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalDataFilter> goalDataFilters;
    private final GoalValidator goalValidator;
    private final GoalAssignmentHelper goalAssignmentHelper;

    public CreateGoalResponse createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateActiveGoalsLimit(userId);
        goalValidator.validateSkillsExist(goalDto.getSkillIds());

        Goal createdGoal = goalRepository.create(
                goalDto.getTitle(),
                goalDto.getDescription(),
                goalDto.getParentId()
        );

        goalAssignmentHelper.bindSkillsToGoal(goalDto.getSkillIds(), createdGoal);
        goalRepository.save(createdGoal);

        return goalMapper.toCreateResponse(createdGoal);
    }

    @Transactional
    public UpdateGoalResponse updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalValidator.findGoalById(goalId);
        goalValidator.validateGoalUpdatable(existingGoal);
        goalValidator.validateSkillsExist(goalDto.getSkillIds());

        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());
        existingGoal.setStatus(goalDto.getStatus());

        goalAssignmentHelper.bindSkillsToGoal(goalDto.getSkillIds(), existingGoal);

        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            goalAssignmentHelper.assignSkillsToUsers(existingGoal, goalDto.getSkillIds());
        }

        goalRepository.save(existingGoal);
        return goalMapper.toUpdateResponse(existingGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = goalValidator.findGoalById(goalId);
        goalRepository.delete(goal);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        return goalDataFilters.stream()
                .filter(filterImpl -> filterImpl.isApplicable(filter))
                .reduce(
                        goalRepository.findByParent(goalId),
                        (stream, filterImpl) -> filterImpl.apply(stream, filter),
                        (s1, s2) -> s2
                )
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        return goalDataFilters.stream()
                .filter(filterImpl -> filterImpl.isApplicable(filter))
                .reduce(
                        goalRepository.findGoalsByUserId(userId),
                        (stream, filterImpl) -> filterImpl.apply(stream, filter),
                        (s1, s2) -> s2
                )
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
    }
}