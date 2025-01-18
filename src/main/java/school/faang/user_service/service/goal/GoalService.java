package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.stream.Stream;

@Service
public class GoalService {
    private final int MAX_GOALS = 3;
    private final SkillService skillService;
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Autowired
    public GoalService(GoalRepository goalRepository, SkillService skillService, SkillRepository skillRepository, GoalMapper goalMapper) {
        this.goalRepository = goalRepository;
        this.skillService = skillService;
        this.skillRepository = skillRepository;
        this.goalMapper = goalMapper;
    }

    public void createGoal(Long userId, Goal goal) {
        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);

        if (activeGoalsCount >= MAX_GOALS) {
            throw new IllegalStateException("Пользователь не может иметь больше 3 активных целей.");
        }

        List<Long> skillIds = goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();

        if (skillService.areSkillsValid(skillIds)) {
            throw new IllegalStateException("Некоторые скиллы, указанные в цели, не существуют в базе данных.");
        }

        Goal newGoal = goalRepository.create(goal.getTitle(), goal.getDescription(),
                goal.getParent() != null ? goal.getParent().getId() : null);

        newGoal.setSkillsToAchieve(skillRepository.findSkillsByGoalId(newGoal.getId()));
    }


    public void updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalStateException("Цель с указанным ID не найдена."));

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя обновить завершённую цель.");
        }

        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new IllegalStateException("Цель должна иметь название.");
        }

        List<Long> skillIds = goalDto.getSkillsToAchieve().stream()
                .map(SkillDto::getId)
                .toList();

        if (skillService.areSkillsValid(skillIds)) {
            throw new IllegalStateException("Некоторые навыки, указанные в цели, не существуют в базе данных.");
        }

        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            List<User> participants = goalRepository.findUsersByGoalId(goalId);
            for (User user : participants) {
                for (Long skillId : skillIds) {
                    skillRepository.assignSkillToUser(skillId, user.getId());
                }
            }
        }

        goalRepository.removeSkillsFromGoal(goalId);
        for (Long skillId : skillIds) {
            goalRepository.addSkillToGoal(skillId, goalId);
        }

        Goal updatedGoal = goalMapper.toEntity(goalDto);
        updatedGoal.setId(goalId);
        goalRepository.save(updatedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalStateException("Цель с указанным ID не найдена."));

        if (goalRepository.countUsersSharingGoal(goalId) > 0) {
            throw new IllegalStateException("Невозможно удалить цель, так как она используется другими пользователями.");
        }

        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.delete(goal);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        List<Goal> subtasks = goalRepository.findByParent(goalId).toList();

        List<Goal> filteredSubtasks = subtasks.stream()
                .filter(goal -> goal.getStatus() != GoalStatus.COMPLETED)
                .toList();

        return filteredSubtasks.stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            goals = goals.filter(goal -> goal.getTitle().contains(filter.getTitle()));
        }
        if (filter.getStatus() != null) {
            goals = goals.filter(goal -> goal.getStatus() == filter.getStatus());
        }
        if (filter.getSkillId() != null) {
            goals = goals.filter(goal -> goal.getSkillsToAchieve().stream()
                    .anyMatch(skill -> skill.getId() == filter.getSkillId()));
        }

        return goals.map(goalMapper::toDto).toList();
    }
}