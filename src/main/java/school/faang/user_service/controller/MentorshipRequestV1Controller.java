package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.message.event.MentorshipEvent;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentorship-requests")
@RequiredArgsConstructor
@Validated
public class MentorshipRequestV1Controller {

    private final UserContext userContext;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validateReceiverAndActorIdsAreNotTheSame(mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filterDto) {
        return mentorshipRequestService.getRequests(filterDto);
    }

    @PutMapping("/{requestId}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable @Positive long requestId) {
        MentorshipRequestDto mentorshipRequestDto =  mentorshipRequestService.acceptRequest(requestId);

        MentorshipEvent mentorshipEvent = new MentorshipEvent(mentorshipRequestDto.requesterId());
        mentorshipRequestService.publishMentorshipEventAsync(mentorshipEvent);

        return mentorshipRequestDto;
    }

    @PutMapping("/{requestId}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable @Positive long requestId,
                                              @Valid @RequestBody RejectionDto rejectionDto) {
        return mentorshipRequestService.rejectRequest(requestId, rejectionDto);
    }
}
