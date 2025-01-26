package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MentorshipServiceImpl {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;

    public List<UserDto> getMentees(long userId) {
        return Optional.ofNullable(userRepository.findById(userId))
                .map(user -> userMapper.toDto(mentorshipRepository.findMenteesById(userId)))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    public List<UserDto> getMentors(long userId) {
        return Optional.ofNullable(userRepository.findById(userId))
                .map(user -> userMapper.toDto(mentorshipRepository.findMentorsById(userId)))
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userRepository.getUserById(mentorId);

        if (mentor != null) {
            mentor.setMentees(
                    mentorshipRepository.findMenteesById(mentorId).stream()
                            .filter(mentee -> mentee.getId() != menteeId)
                            .toList()
            );
        }
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userRepository.getUserById(menteeId);

        if (mentee != null) {
            mentee.setMentors(
                    mentorshipRepository.findMentorsById(mentorId).stream()
                            .filter(mentor -> mentor.getId() != mentorId)
                            .toList()
            );
        }
    }

}
