package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.service.BecomeMentorshipService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final BecomeMentorshipService becomeMentorshipService;

    public List<UserMentorshipDto> getMentees(long mentorId) {
        return becomeMentorshipService.getMentees(mentorId);
    }

    public List<UserMentorshipDto> getMentors(long userId) {
        return becomeMentorshipService.getMentors(userId);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        becomeMentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        becomeMentorshipService.deleteMentor(menteeId, mentorId);
    }
}

