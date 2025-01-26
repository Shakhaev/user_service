package school.faang.user_service.service.mentorship;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class MentorshipServiceImpl {
    private final UserMapper userMapper;

    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;

    public List<UserDto> getMentees(long userId) {
        return userMapper.toDto(mentorshipRepository.findMenteesById(userId));
    }

    public List<UserDto> getMentors(long userId) {
        return userMapper.toDto(mentorshipRepository.findMentorsById(userId));
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userRepository.getUserById(mentorId);

        mentor.setMentees(
                mentorshipRepository.findMenteesById(mentorId).stream()
                        .filter(mentee -> mentee.getId() != menteeId)
                        .toList()
        );
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userRepository.getUserById(menteeId);

        mentee.setMentors(
                mentorshipRepository.findMentorsById(mentorId).stream()
                        .filter(mentor -> mentor.getId() != mentorId)
                        .toList()
        );
    }

}
