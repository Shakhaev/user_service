package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Transactional
    public void deactivateMentor(Long userId){
        User mentor = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с айди " + userId + " не найден"));

        removeMentorFromMentees(mentor);
        reassignGoals(mentor);

        mentor.getMentees().clear();
        userRepository.save(mentor);
    }

    private void reassignGoals(User mentor) {
        mentor.getGoals().forEach(goal -> {
            goal.setMentor(null);
            goalRepository.save(goal);
        });
    }

    private void removeMentorFromMentees(User mentor) {
        mentor.getMentees().forEach((mentee) -> mentee.getMentors().remove(mentor));
    }
}
