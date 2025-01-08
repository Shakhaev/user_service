package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDTO;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.BadRequestException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

    private static final int MAX_ACTIVE_GOAL = 3;

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillService skillService;
    private final UserService userService;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public GoalDTO createGoal(Long userId, GoalDTO goalDTO) {
        validateGoal(goalDTO);

        if (goalRepository.countActiveGoalsPerUser(userId) > MAX_ACTIVE_GOAL) {
            throw new BadRequestException("User have more than " + MAX_ACTIVE_GOAL + " active goals");
        }
        Goal goal = goalMapper.toEntity(goalDTO);
        if (goalDTO.getParentId() != null) {
            Goal parent = findById(goalDTO.getParentId());
            goal.setParent(parent);
        }
        if (goalDTO.getMentorId() != null) {
            User mentor = userService.findById(userId);
            goal.setMentor(mentor);
        }
        goal.setStatus(GoalStatus.ACTIVE);
        User user = userService.findById(userId);
        goal.addUser(user);
        List<Skill> skills = skillService.findByIds(goalDTO.getSkillToAchieveIds());
        skills.forEach(goal::addSkill);
        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public GoalDTO updateGoal(Long goalId, GoalDTO goalDTO) {
        validateGoal(goalDTO);
        Goal goal = findById(goalId);

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new BadRequestException("This goal is already completed");
        }
        goal.setTitle(goalDTO.getTitle());
        goal.setDescription(goalDTO.getDescription());
        goal.setDeadline(goalDTO.getDeadline());
        updateSkills(goal, goalDTO.getSkillToAchieveIds());

        if (goalDTO.getStatus() != null
                && Objects.equals(goalDTO.getStatus(), GoalStatus.COMPLETED.name())
        ) {
            List<User> users = goal.getUsers();
            for (Skill skill : goal.getSkillsToAchieve()) {
                for (User user : users) {
                    if (!user.getSkills().contains(skill)) {
                        skillService.assignSkillsToUser(skill.getId(), user.getId()); // TODO N+1 problem
                    }
                }
            }
            goal.setStatus(GoalStatus.COMPLETED);
        }
        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long id) {
        Goal goal = findById(id);
        Stream<Goal> parent = goalRepository.findByParent(goal.getId());
        parent.forEach(e -> e.setParent(null));
        goalRepository.delete(goal);
    }

    public List<GoalDTO> getGoalsByUser(Long userId, GoalFilterDTO goalFilterDTO) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();

        for (GoalFilter filter : goalFilters) {
            if (filter.isApplicable(goalFilterDTO)) {
                goals = filter.apply(goals, goalFilterDTO);
            }
        }
        return goalMapper.toDtoList(goals);

    }

    public List<GoalDTO> getSubGoals(Long parentId, GoalFilterDTO goalFilterDTO) {
        List<Goal> parent = goalRepository.findByParent(parentId).toList();
        for (GoalFilter filter : goalFilters) {
            if (filter.isApplicable(goalFilterDTO)) {
                parent = filter.apply(parent, goalFilterDTO);
            }
        }
        return goalMapper.toDtoList(parent);
    }

    public Goal findById(Long id) {
        return goalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Goal not found by id: " + id));
    }

    private void updateSkills(Goal goal, List<Long> newSkillIds) {
        List<Long> currentSkillIds = goal.getSkillsToAchieve()
                .stream()
                .map(Skill::getId)
                .toList();

        if (new HashSet<>(currentSkillIds).containsAll(newSkillIds) && new HashSet<>(newSkillIds).containsAll(currentSkillIds)) {
            return;
        }

        goal.getSkillsToAchieve().clear();
        List<Skill> newSkills = skillService.findByIds(newSkillIds);
        newSkills.forEach(goal::addSkill);
    }

    private void validateGoal(GoalDTO goalDTO) {
        if (!skillService.skillsExist(goalDTO.getSkillToAchieveIds())) {
            throw new ResourceNotFoundException("Unable to find skills");
        }

    }


}
