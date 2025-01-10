package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CreateGoalDto;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.exception.UserGoalLimitExceededException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filters;

    private final static int USER_GOAL_MAX_COUNT = 3;
    private final UserRepository userRepository;

    @Transactional
    public GoalDto createGoal(long userId, CreateGoalDto goalDto) {
        userValid(userId);

        if (goalRepository.countActiveGoalsPerUser(userId) >= USER_GOAL_MAX_COUNT) {
            throw new UserGoalLimitExceededException("пользователь может иметь не больше + " + USER_GOAL_MAX_COUNT + " целей");
        }

        List<Skill> skillsToGoal = getSkillsFromDto(goalDto);

        Goal goal = goalRepository.create(goalDto.title(), goalDto.description(), goalDto.parentId());
        goal.setSkillsToAchieve(skillsToGoal);
        goal = goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    private List<Skill> getSkillsFromDto(CreateGoalDto goalDto) {
        if (goalDto.skillsToAchieveIds() != null) {
            return goalDto.skillsToAchieveIds().stream()
                    .map(id -> skillRepository.findById(id).orElseThrow(
                            () -> new SkillNotFoundException("Скилл с ID: " + id + " не существует")))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private List<Skill> getSkillsFromDto(GoalDto goalDto) {
        if (goalDto.skillsToAchieveIds() != null) {
            return goalDto.skillsToAchieveIds().stream()
                    .map(id -> skillRepository.findById(id).orElseThrow(
                            () -> new SkillNotFoundException("Скилл с ID: " + id + " не существует")))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Transactional
    public GoalDto updateGoal(GoalDto goalDto) {
        Goal goal = getGoalById(goalDto.id());

        List<Skill> skillsToGoal = getSkillsFromDto(goalDto);
        GoalStatus currentStatus = goal.getStatus();

        if (!skillsToGoal.isEmpty()) {
            goal.setSkillsToAchieve(skillsToGoal);
        }

        goalMapper.updateEntityFromDto(goalDto, goal);

        if (goal.getStatus() == GoalStatus.COMPLETED && currentStatus == GoalStatus.ACTIVE) {
            goal.getSkillsToAchieve().forEach(skill ->
                    skill.getUsers().forEach(user ->
                            skillRepository.assignSkillToUser(skill.getId(), user.getId())
                    )
            );
        }

        goal = goalRepository.save(goal);
        return goalMapper.toDto(goal);
    }

    public void deleteGoal(long goalId) {
        Goal goal = getGoalById(goalId);
        goalRepository.delete(goal);
    }

    public List<GoalDto> getSubtasksGoal(long goalId) {
        getGoalById(goalId);

        return goalRepository.findByParent(goalId)
                .map(g -> goalMapper.toDto(g))
                .toList();
    }

    private Goal getGoalById(Long goalId) {
        if (goalId == null) {
            throw new IllegalArgumentException("Нет ID");
        }

        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Данной цели не существует"));
    }

    public List<GoalDto> getGoals(long userId, GoalFilterDto filterDto) {
        userValid(userId);

        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);
        return filterGoals(goalStream, filterDto)
                .map(g -> goalMapper.toDto(g))
                .toList();
    }

    private void userValid(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не существует");
        }
    }

    private Stream<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        for (GoalFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                goals = filter.apply(goals, filterDto);
            }
        }

        return goals;
    }
}
