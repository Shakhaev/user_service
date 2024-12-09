package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidation;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    public Goal findGoalById(Long id) {
        return goalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Goal not found by id: %s", id)));
    }

    public GoalDto createGoal(Long userId, GoalDto goal) {
        goalValidation.validateGoalRequest(userId, goal, true);

        goal.setId(null);
        goal.setStatus(GoalStatus.ACTIVE);

        Goal entity = goalMapper.toEntity(goal);

        Optional<User> user = userService.getUserById(userId);
        user.ifPresent(value -> entity.setUsers(List.of(value)));

        if (goal.getSkillIds() != null) {
            entity.setSkillsToAchieve(goal.getSkillIds().stream()
                    .map(skillService::getSkillById)
                    .toList());
        } else {
            entity.setSkillsToAchieve(Collections.emptyList());
        }

        goalRepository.save(entity);

        return goalMapper.toDto(entity);
    }

    public GoalDto updateGoal(Long userId, GoalDto goal) {
        goalValidation.validateGoalRequest(userId, goal, false);

        Optional<Goal> optionalEntity = goalRepository.findById(goal.getId());

        if (optionalEntity.isEmpty()) {
            throw new EntityNotFoundException("Goal with id " + goal.getId() + " does not exist");
        }

        Goal entity = optionalEntity.get();

        entity.setTitle(goal.getTitle());
        entity.setDescription(goal.getDescription());
        entity.setStatus(goal.getStatus());
        entity.setDeadline(goal.getDeadline());

        if (goal.getMentorId() != null) {
            Optional<User> mentor = userService.getUserById(goal.getMentorId());
            mentor.ifPresent(entity::setMentor);
        }

        if (goal.getParentGoalId() != null) {
            entity.setParent(goalRepository.findById(goal.getParentGoalId()).orElseThrow(() ->
                    new EntityNotFoundException("Parent goal with id " + goal.getParentGoalId() + " does not exist")));
        }

        goalRepository.save(entity);

        return goalMapper.toDto(entity);
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId).stream();

        Stream<Goal> filteredGoals = goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        goals,
                        (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s1
                );

        return filteredGoals.map(goalMapper::toDto).toList();
    }

    public GoalDto completeTheGoal(long userId, long goalId) {
        Goal goalToComplete = userService.findUserById(userId).getGoals().stream()
                .filter(goal -> goal.getId().equals(goalId))
                .findFirst().orElseThrow(() ->
                        new EntityNotFoundException("Goal with id " + goalId + " does not exist"));
        goalToComplete.setStatus(GoalStatus.COMPLETED);
        goalRepository.save(goalToComplete);
        goalCompletedEventPublisher.publish(new GoalCompletedEvent(userId, goalId, LocalDateTime.now()));
        return goalMapper.toDto(goalToComplete);
    }
}
