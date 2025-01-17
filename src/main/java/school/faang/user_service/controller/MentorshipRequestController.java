package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.filter.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestServiceImpl;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestServiceImpl service;

    @PostMapping
    public MentorshipResponseDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        log.info("#requestMentorship: mentorship request has been received from user with id: {}", mentorshipRequestDto.receiverUserId());
        if (Objects.isNull(mentorshipRequestDto.description()) || mentorshipRequestDto.description().isBlank()) {
            throw new IllegalArgumentException(String.format("Запрос от пользователя с id: %d не содержит описания." +
                    "Описание запроса не может быть пустым.", mentorshipRequestDto.requesterUserId()));
        }
        return service.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/requests")
    public List<MentorshipResponseDto> getRequests(@RequestBody RequestFilterDto filter) {
        log.info("#getRequests: request has been received to receive all mentoring requests that match the filters");
        return service.getRequests(filter);
    }

    @PutMapping("/requests/{id}/accept")
    public void acceptRequest(@PathVariable("id") long requestId) {
        log.info("#acceptRequest: request has been received to accept a mentoring request from user with id: {}", requestId);
        service.acceptRequest(requestId);
    }

    @PutMapping("/requests/{id}/reject")
    public void rejectRequest(@PathVariable("id") long requestId, @RequestBody RejectionDto rejection) {
        log.info("#rejectRequest: request has been received to reject a mentoring request from user with id: {}", requestId);
        service.rejectRequest(requestId, rejection);
    }
}
