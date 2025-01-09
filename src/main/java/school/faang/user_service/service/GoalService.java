package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.exception.UserGoalLimitExceededException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;

    private final static int USER_GOAL_MAX_COUNT = 3;

    @Transactional
    public GoalDto createGoal(long userId, GoalDto goalDto) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= USER_GOAL_MAX_COUNT) {
            throw new UserGoalLimitExceededException("пользователь может иметь не больше + " + USER_GOAL_MAX_COUNT + " целей");
        }

        List<Skill> skillsToGoal = getSkillsFromDto(goalDto);

        Goal goal = goalRepository.create(goalDto.title(), goalDto.description(), goalDto.parentId());
        goal.setSkillsToAchieve(skillsToGoal);
        goal = goalRepository.save(goal);

        return goalMapper.toDto(goal);
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
        if (goalDto.id() == null) {
            throw new IllegalArgumentException("Нет ID");
        }

        List<Skill> skillsToGoal = getSkillsFromDto(goalDto);
        Goal goal = goalRepository.findById(goalDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Данной цели не существует"));

        GoalStatus currentStatus = goal.getStatus();

        if (!skillsToGoal.isEmpty()){
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
}
