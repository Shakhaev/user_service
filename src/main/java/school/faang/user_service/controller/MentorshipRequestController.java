package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.service.impl.MentorshipRequestServiceImpl;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentorship/requests")
public class MentorshipRequestController {
    private final MentorshipRequestServiceImpl service;

    @PostMapping
    public MentorshipResponseDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        log.info("#requestMentorship: mentorship request has been received from user with id: {}", mentorshipRequestDto.receiver().getUserId());
        if (Objects.isNull(mentorshipRequestDto.description()) || mentorshipRequestDto.description().isBlank()) {
            throw new IllegalArgumentException(String.format("Request from user with id: %d does not contain a description." +
                    "The description cannot be missing or empty.", mentorshipRequestDto.requester().getUserId()));
        }
        return service.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping
    public List<MentorshipResponseDto> getRequests(MentorshipRequestFilterDto filter) {
        log.info("#getRequests: request has been received to receive all mentoring requests that match the filters");
        return service.getRequests(filter);
    }

    @PutMapping("/{id}/accept")
    public void acceptRequest(@PathVariable("id") long requestId) {
        log.info("#acceptRequest: request has been received to accept a mentoring request from user with id: {}", requestId);
        service.acceptRequest(requestId);
    }

    @PutMapping("/{id}/reject")
    public void rejectRequest(@PathVariable("id") long requestId, @RequestBody RejectionDto rejection) {
        log.info("#rejectRequest: request has been received to reject a mentoring request from user with id: {}", requestId);
        service.rejectRequest(requestId, rejection);
    }
}
