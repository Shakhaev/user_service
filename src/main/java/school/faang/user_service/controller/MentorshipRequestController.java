package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.filter.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestServiceImpl service;

    @PostMapping
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription().isBlank()) {
            throw new RuntimeException("Описание запроса не может быть пустым");
        }
        return service.requestMentorship(mentorshipRequestDto);
    }

    @PostMapping("/requests")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return service.getRequests(filter);
    }

    @PutMapping("/accept/{id}")
    public void acceptRequest(@PathVariable("id") long requestId) {
        service.acceptRequest(requestId);
    }

    @PutMapping("/reject/{id}")
    public void rejectRequest(@PathVariable("id") long requestId, @RequestBody RejectionDto rejection) {
        service.rejectRequest(requestId, rejection);
    }
}
