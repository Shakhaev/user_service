package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserMentorshipDto;
import school.faang.user_service.service.MentorshipRelationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipRelationService mentorshipRelationService;

    public List<UserMentorshipDto> getMentees(long mentorId) {
        return mentorshipRelationService.getMentees(mentorId);
    }

    public List<UserMentorshipDto> getMentors(long userId) {
        return mentorshipRelationService.getMentors(userId);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipRelationService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipRelationService.deleteMentor(menteeId, mentorId);
    }
}

