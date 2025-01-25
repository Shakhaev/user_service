package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getMentees(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " +
                        userId + " not found"));

        List<User> userMentees = user.getMentees();
        if (userMentees == null) {
            return new ArrayList<>();
        }

        List<UserDto> mentees = new ArrayList<>();
        userMentees.forEach(mentee -> mentees.add(
                userMapper.toRecommendationRequestDto(mentee)));

        return mentees;
    }

    @Override
    public List<UserDto> getMentors(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " +
                        userId + " not found"));

        List<User> userMentors = user.getMentors();
        if (userMentors == null) {
            return new ArrayList<>();
        }

        List<UserDto> mentors = new ArrayList<>();
        userMentors.forEach(mentor -> mentors.add(
                userMapper.toRecommendationRequestDto(mentor)));

        return mentors;
    }

    @Override
    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor with id " +
                        mentorId + " not found"));

        mentor.getMentees().removeIf(menteeToRemove -> menteeToRemove.
                getId().
                equals(menteeId));
        userRepository.save(mentor);
    }

    @Override
    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee with id " +
                        menteeId + " not found"));

        mentee.getMentors().removeIf(mentorToRemove -> mentorToRemove.
                getId().
                equals(mentorId));
        userRepository.save(mentee);

    }
}
