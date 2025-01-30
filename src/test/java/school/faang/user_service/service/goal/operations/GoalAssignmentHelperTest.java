package school.faang.user_service.service.goal.operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalAssignmentHelperTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalRepository goalRepository;

    private Goal goal;
    private List<Long> skillIds;
    private List<Skill> skills;

    @InjectMocks
    private GoalAssignmentHelper goalAssignmentHelper;

    @BeforeEach
    void setup() {
        goal = new Goal();
        goal.setId(1L);
        goal.setSkillsToAchieve(new ArrayList<>());

        skillIds = List.of(1L, 2L, 3L);

        skills = new ArrayList<>();
        for (Long id : skillIds) {
            Skill skill = new Skill();
            skill.setId(id);
            skills.add(skill);
        }
    }

    @Test
    void testBindSkillsToGoal_AddsNewSkills() {
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        goalAssignmentHelper.bindSkillsToGoal(skillIds, goal);

        verify(skillRepository).findAllById(skillIds);
        Set<Skill> skillSet = new HashSet<>(goal.getSkillsToAchieve());
        assert skillSet.containsAll(skills);
    }

    @Test
    void testBindSkillsToGoal_RemovesOldSkills() {
        Skill oldSkill = new Skill();
        oldSkill.setId(4L);
        goal.getSkillsToAchieve().add(oldSkill);

        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        goalAssignmentHelper.bindSkillsToGoal(skillIds, goal);

        verify(skillRepository).findAllById(skillIds);
        assert !goal.getSkillsToAchieve().contains(oldSkill);
        assert goal.getSkillsToAchieve().containsAll(skills);
    }

    @Test
    void testAssignSkillsToUsers_AddsSkillsToUsers() {
        User user1 = new User();
        user1.setSkills(new ArrayList<>());

        User user2 = new User();
        user2.setSkills(new ArrayList<>());

        List<User> users = List.of(user1, user2);

        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(users);
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        goalAssignmentHelper.assignSkillsToUsers(goal, skillIds);

        verify(goalRepository).findUsersByGoalId(goal.getId());
        verify(skillRepository).findAllById(skillIds);

        assert user1.getSkills().containsAll(skills);
        assert user2.getSkills().containsAll(skills);
    }

    @Test
    void testAssignSkillsToUsers_DoesNothingIfNoUsers() {
        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(List.of());

        goalAssignmentHelper.assignSkillsToUsers(goal, skillIds);

        verify(goalRepository).findUsersByGoalId(goal.getId());
        verify(skillRepository, never()).findAllById(anyList());
    }
}