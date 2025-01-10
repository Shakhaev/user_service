package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/api/mentorship/")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees")
    public List<User> getMentees(long id) {
        return mentorshipService.getMentees(id);
    }

    @GetMapping("/mentors")
    public List<User> getMentors(long id) {
        return mentorshipService.getMentors(id);
    }

    @PostMapping("/remove-mentee/{mentorId}/{menteeId}")
    public void deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @PostMapping("/remove-mentor/{menteeId}/{mentorId}")
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
