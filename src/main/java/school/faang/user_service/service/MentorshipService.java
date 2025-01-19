package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;

    public void removeMentorship(long mentorId) {
        User mentor = userRepository.findById(mentorId).orElseThrow(() ->
                new IllegalArgumentException("Не удалось получить пользователя по айди " + mentorId));
        mentor.getMentees().forEach(mentee -> {
            mentee.setMentors(mentee.getMentors().stream().filter(mntr -> mntr.getId() != mentorId).toList());
            mentee.setGoals(mentee.getGoals().stream().map(goal -> {
                if (goal.getMentor().getId() == mentorId) {
                    goal.setMentor(null);
                }
                return goal;
            }).toList());
            userRepository.save(mentee);
        });
        mentor.setMentees(Collections.emptyList());
        userRepository.save(mentor);
    }
}
