package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public MentorshipRequestDto acceptRequest(@PathVariable @Positive long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    public MentorshipRequestDto rejectRequest(@PathVariable @Positive long id,
                                              @RequestBody @Valid RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(id, rejectionDto);
    }
}
