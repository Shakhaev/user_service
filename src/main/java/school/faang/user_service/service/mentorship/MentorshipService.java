package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeReadDto;
import school.faang.user_service.dto.mentorship.MentorReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MenteeReadMapper;
import school.faang.user_service.mapper.mentorship.MentorReadMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final MenteeReadMapper menteeReadMapper;
    private final MentorReadMapper mentorReadMapper;

    public List<MenteeReadDto> getMentees(long userId) {
        User mentor = getUser(userId);

        List<MenteeReadDto> mentees = mentor.getMentees().stream()
                .map(menteeReadMapper::toDto)
                .toList();

        return mentees;
    }

    public List<MentorReadDto> getMentors(long userId) {
        User mentee = getUser(userId);

        List<MentorReadDto> mentors = mentee.getMentors().stream()
                .map(mentorReadMapper::toDto)
                .toList();

        return mentors;
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = getUser(mentorId);

        mentor.getMentees().removeIf(mentee -> mentee.getId().equals(menteeId));

        userRepository.save(mentor);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        User mentee = getUser(menteeId);

        mentee.getMentors().removeIf(mentor -> mentor.getId().equals(mentorId));

        userRepository.save(mentee);
    }

    private User getUser(long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(() -> {
            String message = "Пользователь с ID " + userId + " не найден";
            log.warn(message);
            return new EntityNotFoundException(message);
        });
    }
}
