package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(Long userId) {
        if (userId == null) {
            throwError("User ID is missing. Cannot search mentees.");
        }
        List<UserDto> users= mentorshipService.getMentees(userId);
        log.info("Found {} mentees.", users.size());
        return users;
    }

    public List<UserDto> getMentors(Long userId) {
        if (userId == null) {
            throwError("User ID is missing. Cannot search mentors.");
        }
        List<UserDto> users= mentorshipService.getMentors(userId);
        log.info("Found {} mentors.", users.size());
        return users;
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        if (menteeId == null || mentorId == null) {
            throwError("User ID is missing. Cannot delete mentees.");
        }
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        if (menteeId == null || mentorId == null) {
            throwError("User ID is missing. Cannot delete mentors.");
        }
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    private void throwError(String errorMessage) {
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }
}
