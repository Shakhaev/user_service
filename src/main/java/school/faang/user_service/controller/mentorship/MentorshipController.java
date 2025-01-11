package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserMentorshipDto> getMentees(long mentorId) { return mentorshipService.getMentees(mentorId); }

    public List<UserMentorshipDto> getMentors(long userId) { return mentorshipService.getMentors(userId); }

    public void deleteMentee(long mentorId, long menteeId) { mentorshipService.deleteMentee(menteeId, mentorId); }

    public void deleteMentor(long menteeId, long mentorId) { mentorshipService.deleteMentor(menteeId, mentorId); }
}
