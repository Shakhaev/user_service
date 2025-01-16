package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null ||
                mentorshipRequestDto.getDescription().isEmpty()) {
            throw new DataValidationException("Required data is absent");
        } else {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        }
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
    }

    public void rejectRequest(long requestId, RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}
