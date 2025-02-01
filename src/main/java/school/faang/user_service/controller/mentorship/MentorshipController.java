package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MenteeReadDto;
import school.faang.user_service.dto.mentorship.MentorReadDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<MenteeReadDto> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<MentorReadDto> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long mentorId, long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
    }
}
