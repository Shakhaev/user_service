package school.faang.user_service.controller.mentorship;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MentorshipService {

    private final UserRepository userRepository;

    @Autowired
    public MentorshipService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    public List<User> getMentees(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<User> userMentees = new ArrayList<>();
        Optional<List<User>> mentees = Optional.ofNullable(user.get().getMentees());
        mentees.ifPresent(userMentees::addAll);
        return userMentees;
    }

    @NonNull
    public List<User> getMentors(long userId) {


        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<User> userMentors = new ArrayList<>();
        Optional<List<User>> mentors = Optional.ofNullable(user.get().getMentors());
        mentors.ifPresent(userMentors::addAll);
        return userMentors;
    }

    private boolean mentorAndMenteeIsNotExists(long menteeId, long mentorId) {
        Optional<User> mentee = userRepository.findById(menteeId);

        if (mentee.isEmpty()) {
            throw new RuntimeException("Mentor not found");
        }

        Optional<User> mentor = userRepository.findById(mentorId);
        if (mentor.isEmpty()) {
            throw new RuntimeException("Mentee not found");
        }
        return true;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        if (mentorAndMenteeIsNotExists(menteeId, mentorId)) {
            throw new RuntimeException("Mentor and Mentee not found!");
        }
        Optional<User> mentor = userRepository.findById(mentorId);
        mentor.get().getMentees().removeIf(menteeToRemove -> menteeToRemove.getId().equals(menteeId));
    }

    public void deleteMentor(long menteeId, long mentorId) {
        if (mentorAndMenteeIsNotExists(menteeId, mentorId)) {
            throw new RuntimeException("Mentor and Mentee not found!");
        }
        Optional<User> mentee = userRepository.findById(menteeId);
        mentee.get().getMentors().removeIf(mentorToRemove -> mentorToRemove.getId().equals(mentorId));

    }
}
