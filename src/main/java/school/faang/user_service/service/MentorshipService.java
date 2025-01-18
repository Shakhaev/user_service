package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MentorshipService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Autowired
    public MentorshipService(UserRepository userRepository, GoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    public void removeMenteeFromUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserValidationException("User not found"));
        user.getMentees().forEach(mentee -> {
            mentee.getMentors().remove(user);
            userRepository.save(mentee);
        });
    }

    public void removeMenteeGoals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserValidationException("User not found"));
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