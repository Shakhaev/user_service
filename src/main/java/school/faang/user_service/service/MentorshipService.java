package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper mentorshipResponseMapper;

    private List<MentorshipResponseDto> getMentorshipList(long userId, Function<User, List<User>> relationshipFunction) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<User> mentorshipList = relationshipFunction.apply(user);
        if (mentorshipList == null) {
            mentorshipList = new ArrayList<>();
        }
        return mentorshipResponseMapper.toMentorshipDtos(mentorshipList);
    }

    public List<MentorshipResponseDto> getMentees(long userId) {
        return getMentorshipList(userId, User::getMentees);
    }

    public List<MentorshipResponseDto> getMentors(long userId) {
        return getMentorshipList(userId, User::getMentors);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        User user = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getMentees() == null || user.getMentees().isEmpty()) {
            throw new IllegalStateException("User has no mentees to remove");
        }
        boolean removed = user.getMentees().removeIf(mentee -> mentee.getId() == menteeId);

        if (!removed) {
            throw new IllegalArgumentException("Mentee not found in user's mentee list");
        }

        userRepository.save(user);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User user = userRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getMentors() == null || user.getMentors().isEmpty()) {
            throw new IllegalStateException("User has no mentors to remove");
        }
        boolean removed = user.getMentors().removeIf(mentor -> mentor.getId() == mentorId);

        if (!removed) {
            throw new IllegalArgumentException("Mentor not found in user's mentors list");
        }

        userRepository.save(user);
    }
}
