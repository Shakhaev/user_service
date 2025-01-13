package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship-requests")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping("/requests")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping("/accept-request/{id}")
    public MentorshipRequestDto acceptRequest(@PathVariable @Positive long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/reject-request/{id}")
    public MentorshipRequestDto rejectRequest(@PathVariable @Positive long id,
                                              @RequestBody @Valid RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(id, rejectionDto);
    }
}
