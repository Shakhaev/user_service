package school.faang.user_service.controller.mentorship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final UserRepository userRepository;

    public List<User> getMentees(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> userMentees = new ArrayList<>();
        Optional<List<User>> mentees = Optional.ofNullable(user.getMentees());
        mentees.ifPresent(userMentees::addAll);
        return userMentees;
    }

    public List<User> getMentors(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        List<User> userMentors = new ArrayList<>();
        Optional<List<User>> mentors = Optional.ofNullable(user.getMentors());
        mentors.ifPresent(userMentors::addAll);
        return userMentors;
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor with id " + mentorId + " not found"));

        mentor.getMentees().removeIf(menteeToRemove -> menteeToRemove.getId().equals(menteeId));
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee with id " + menteeId + " not found"));

        mentee.getMentors().removeIf(mentorToRemove -> mentorToRemove.getId().equals(mentorId));

    }
}
