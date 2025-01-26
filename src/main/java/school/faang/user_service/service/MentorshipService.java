package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MentorshipService {

    private final GoalRepository goalRepository;
    private final UserService userService;

    @Autowired
    public MentorshipService(GoalRepository goalRepository, UserService userService) {
        this.goalRepository = goalRepository;
        this.userService = userService;
    }

    public void removeMenteeFromUser(Long userId) {
        User user = userService.getById(userId);
        user.getMentees().forEach(mentee -> {
            mentee.getMentors().remove(user);
            userService.saveUser(mentee);
        });
    }

    public void removeMenteeGoals(Long userId) {
        User user = userService.getById(userId);
        List<Goal> updatedGoals = new ArrayList<>();
        user.getMentees().forEach(mentee -> {
            List<Goal> menteeGoals = mentee.getSetGoals();

            List<Goal> goalsToUpdate = menteeGoals.stream()
                    .filter(goal -> goal.getMentor() != null && goal.getMentor().getId().equals(userId))
                    .toList();

            goalsToUpdate.forEach(goal -> {
                goal.setMentor(mentee);
                updatedGoals.add(goal);
            });
        });
        goalRepository.saveAll(updatedGoals);
    }
}