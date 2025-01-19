package school.faang.user_service.controller;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.controller.mentorship.MentorshipService;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class MentorshipController {
    private MentorshipService mentorshipService;

    @Autowired
    private void mentorshipService(MentorshipService mentorshipService) {
        this.mentorshipService = mentorshipService;
    }
    @NonNull
    private List<User> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }
    private List<User> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }
    private void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }
    private void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
