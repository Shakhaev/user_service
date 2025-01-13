package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MenteeReadDto;
import school.faang.user_service.dto.mentorship.MentorReadDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{userId}")
    public List<MenteeReadDto> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors/{userId}")
    public List<MentorReadDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/delete-mentee")
    public void deleteMentee(@RequestParam("menteeId") long menteeId, @RequestParam("mentorId") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }
    @DeleteMapping("/delete-mentor")
    public void deleteMentor(@RequestParam("mentorId") long mentorId, @RequestParam("menteeId") long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
