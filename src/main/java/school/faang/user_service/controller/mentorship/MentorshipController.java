package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    public List<User> getMentees(long id) {
        return mentorshipService.getMentees(id);
    }

    public List<User> getMentors(long id) {
        return mentorshipService.getMentors(id);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
