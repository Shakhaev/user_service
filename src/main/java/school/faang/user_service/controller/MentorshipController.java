package school.faang.user_service.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;


        private Logger log;

    @NonNull
    public List<User> getMentees(Long userId) {
        if (userId == null) {
            throwError("User ID is missing. Cannot search mentees.");
        }
        List<User> users= mentorshipService.getMentees(userId);
        log.info("Found {} mentees.", users.size());
        return users;
    }

    public List<User> getMentors(Long userId) {
        if (userId == null) {
            throwError("User ID is missing. Cannot search mentors.");
        }
        List<User> users= mentorshipService.getMentees(userId);
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

    public void setLog(Logger log) {
        this.log = log;
    }
}
