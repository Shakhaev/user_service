package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filters.goal.GoalFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public void createGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new IllegalArgumentException("User not found");
                });

        long numberOfActiveGoals = goalRepository.countActiveGoalsPerUser(userId);

        if (numberOfActiveGoals <= 3) {
            if (goal.getSkillsToAchieve().stream().anyMatch(skill -> skillRepository.existsByTitle(skill.getTitle()))) {

                if (goal.getUsers() == null) {
                    goal.setUsers(new ArrayList<>());
                }

                goal.getUsers().add(user);
                user.getGoals().add(goal);

                goal.setStatus(GoalStatus.ACTIVE);
                goal.setCreatedAt(LocalDateTime.now());

                goalRepository.save(goal);

            } else {
                throw new IllegalArgumentException("The goal contains non-existent skills");
            }
        } else {
            throw new IllegalArgumentException("The user's number of active goals exceeds the maximum number");
        }
    }

    @Transactional
    public void updateGoal(Long goalId, Goal goal) {
        Goal updatedGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (goal.getStatus().equals(GoalStatus.ACTIVE)) {
            if (goal.getSkillsToAchieve().stream().anyMatch(skill -> skillRepository.existsByTitle(skill.getTitle()))) {

                updatedGoal.setParent(goal.getParent());
                updatedGoal.setDescription(goal.getDescription());
                updatedGoal.setDeadline(goal.getDeadline());
                updatedGoal.setInvitations(goal.getInvitations());
                updatedGoal.setStatus(goal.getStatus());

                goal.setUpdatedAt(LocalDateTime.now());

                goalRepository.save(updatedGoal);
                log.info("Goal updated successfully with id: {}", goalId);
            } else {
                throw new IllegalArgumentException("The goal contains non-existent skills");
            }
        } else { // new method
            List<Skill> oldSkillsToAchieve = skillRepository.findSkillsByGoalId(goalId);

            List<User> users = userRepository.findAll();
            for (User user : users) {
                for (Skill skill : oldSkillsToAchieve) {
                    skillRepository.assignSkillToUser(skill.getId(), user.getId());
                }
            }

            updatedGoal.getSkillsToAchieve().forEach(skill -> skillRepository.delete(skill));

            updatedGoal.setSkillsToAchieve(goal.getSkillsToAchieve());
            goalRepository.save(updatedGoal);
            log.info("SkillsToAchieve updated successfully");
        }
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goalRepository.delete(goal);
        log.info("Goal with id {} deleted successfully", goalId);
    }

    @Transactional
    public List<Goal> findSubtasksByGoalId(long parentId, GoalFilterDto filters) {
        Stream<Goal> subtasks = goalRepository.findByParent(parentId);

        log.info("Subtasks by goal with parent id {} before filtering", parentId);
        return filterSubtasksByGoal(subtasks, filters);
    }

    public List<Goal> filterSubtasksByGoal(Stream<Goal> subtasks, GoalFilterDto filters) {
        log.info("Applying filters to subtasks by goal");

        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(subtasks, (currentStream, filter) -> {
                            log.info("Applying filter to subtasks by goal: {}", filter.getClass().getSimpleName());
                            return filter.apply(currentStream, filters);
                        },
                        (s1, s2) -> s1)
                .toList();
    }

    @Transactional
    public List<Goal> getGoalsByUserId(long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        log.info("Goals before filtering");
        return filterGoals(goals, filters);
    }

    public List<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filters) {
        log.info("Applying filters to goals");

        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(goals, (currentStream, filter) -> {
                            log.info("Applying filter: {}", filter.getClass().getSimpleName());
                            return filter.apply(currentStream, filters);
                        },
                        (s1, s2) -> s1)
                .toList();
    }
}