package school.faang.user_service.service.goal.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GoalAssignmentHelper {

    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;

    public void bindSkillsToGoal(List<Long> skillIds, Goal goal) {
        List<Skill> existingSkills = Optional.ofNullable(goal.getSkillsToAchieve()).orElseGet(List::of);
        List<Skill> newSkills = loadSkills(skillIds);

        Set<Skill> newSkillSet = new HashSet<>(newSkills);
        Set<Skill> existingSkillSet = new HashSet<>(existingSkills);

        existingSkillSet.removeAll(newSkillSet);
        existingSkills.forEach(newSkillSet::remove);

        goal.getSkillsToAchieve().removeAll(existingSkillSet);
        goal.getSkillsToAchieve().addAll(newSkillSet);
    }

    public void assignSkillsToUsers(Goal goal, List<Long> skillIds) {
        List<User> users = goalRepository.findUsersByGoalId(goal.getId());

        if (users.isEmpty()) {
            return;
        }

        List<Skill> skills = loadSkills(skillIds);
        for (User user : users) {
            user.getSkills().addAll(skills);
        }
    }

    private List<Skill> loadSkills(List<Long> skillIds) {
        return Optional.ofNullable(skillIds)
                .filter(ids -> !ids.isEmpty())
                .map(skillRepository::findAllById)
                .orElse(List.of());
    }
}