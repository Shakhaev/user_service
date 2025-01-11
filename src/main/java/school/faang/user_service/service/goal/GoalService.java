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
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filters.goal.GoalFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public void createGoal(Long userId, Goal goal) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        long numberOfActiveGoals = goalRepository.countActiveGoalsPerUser(userId);

        if (numberOfActiveGoals <= 3) {
            if (goal.getSkillsToAchieve().stream().anyMatch(skill -> skillService.skillExistsByTitle(skill.getTitle()))) {

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

    public void assignSkillToGoal(long skillId, long goalId) {
        skillService.assignSkillToGoal(skillId, goalId);
    }

    @Transactional
    public void updateGoal(Long goalId, Goal goal) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (existingGoal.getStatus().equals(GoalStatus.ACTIVE)) {
            if (existingGoal.getSkillsToAchieve().stream().anyMatch(skill ->
                    skillService.skillExistsByTitle(skill.getTitle()))) {

                existingGoal.setDescription(goal.getDescription());
                existingGoal.setDeadline(goal.getDeadline());
                existingGoal.setStatus(goal.getStatus());

                existingGoal.setUpdatedAt(LocalDateTime.now());

                goalRepository.save(existingGoal);

            } else {
                throw new IllegalArgumentException("The goal contains non-existent skills");
            }
        } else {
            List<Skill> oldSkillsToAchieve = skillService.findSkillsByGoalId(goalId);

            List<User> users = userService.findAllUsers();
            users.stream()
                    .flatMap(user -> oldSkillsToAchieve.stream()
                            .map(skill -> new AbstractMap.SimpleEntry<>(skill, user)))
                    .forEach(entry ->
                            skillService.assignSkillToGoal(entry.getKey().getId(), entry.getValue().getId()));
        }
    }

    public void updateSkillsToGoal(Long goalId, List<Skill> skills) {

        List<Skill> oldSkills = goalRepository.findSkillsByGoalId(goalId);

        if (oldSkills.isEmpty()) {
            throw new IllegalArgumentException("No skills found for the goal");
        }

        oldSkills.forEach(skillService::deleteSkill);

        skills.forEach(skill -> skillService.assignSkillToGoal(skill.getId(), goalId));
    }


    @Transactional
    public void deleteGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goalRepository.delete(goal);
    }

    @Transactional
    public List<Goal> findSubtasksByGoalId(long parentId, GoalFilterDto filters) {
        List<Goal> subtasks = goalRepository.findByParent(parentId).toList();

        if (subtasks.isEmpty()) {
            throw new IllegalArgumentException("No subtasks found for the parent goal");
        }

        log.info("Goal subtasks with parent id {} before filtering", parentId);
        return filterSubtasksByGoal(subtasks.stream(), filters);
    }

    public List<Goal> filterSubtasksByGoal(Stream<Goal> subtasks, GoalFilterDto filters) {
        log.info("Applying filters to subtasks");

        return goalFilters.stream()
                .filter(filter -> filter
                        .isApplicable(filters))
                .reduce(subtasks, (currentStream, filter) -> filter
                        .apply(currentStream, filters), (s1, s2) -> s1)
                .toList();

    }

    @Transactional
    public List<Goal> getGoalsByUserId(long userId, GoalFilterDto filters) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();

        if (goals.isEmpty()) {
            throw new IllegalArgumentException("No goals found for the user");
        }

        log.info("Goals before filtering");
        return filterGoals(goals.stream(), filters);
    }

    public List<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filters) {
        log.info("Applying filters to goals");

        return goalFilters.stream()
                .filter(filter -> filter
                        .isApplicable(filters))
                .reduce(goals, (currentStream, filter) -> filter
                        .apply(currentStream, filters), (s1, s2) -> s1)
                .toList();
    }
}