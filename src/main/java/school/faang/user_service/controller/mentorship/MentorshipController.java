package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentees(long id) {
        return mentorshipService.getMentees(id);
    }

    public List<UserDto> getMentors(long id) {
        return mentorshipService.getMentors(id);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}