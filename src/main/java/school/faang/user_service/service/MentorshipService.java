package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserMentorshipDto> getMentees(long userId) {
        log.info("Fetching mentees for mentor with ID: {}", userId);
        User mentor = mentorshipRepository.findById(userId).orElse(null);
        if (mentor == null || mentor.getMentees() == null) {
            return List.of();
        }
        return mentor.getMentees().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserMentorshipDto> getMentors(long userId) {
        log.info("Fetching mentors for user with ID: {}", userId);
        User mentee = mentorshipRepository.findById(userId).orElse(null);
        if (mentee == null || mentee.getMentors() == null) {
            return List.of();
        }
        return mentee.getMentors().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteMentee(long menteeId, long mentorId) {
        log.info("Deleting mentee with ID: {} for mentor with ID: {}", menteeId, mentorId);
        User mentor = mentorshipRepository.findById(mentorId).orElseThrow(() ->
                new IllegalArgumentException("Mentor not found with ID: " + mentorId));

        User mentee = mentor.getMentees().stream()
                .filter(m -> m.getId().equals(menteeId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Mentee not found with ID: " + menteeId + " for Mentor ID: " + mentorId));

        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        log.info("Deleting mentor with ID: {} for mentee with ID: {}", mentorId, menteeId);
        User mentee = mentorshipRepository.findById(menteeId).orElseThrow(() ->
                new IllegalArgumentException("Mentee not found with ID: " + menteeId));

        User mentor = mentee.getMentors().stream()
                .filter(m -> m.getId().equals(mentorId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Mentor not found with ID: " + mentorId + " for Mentee ID: " + menteeId));

        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }
}
