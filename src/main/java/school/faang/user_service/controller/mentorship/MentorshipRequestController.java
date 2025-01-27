package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mentorship")
@RestController
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper requestMapper;
    private final MentorshipRequestValidator requestValidator;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new DataValidationException("Required data is absent");
        }
        if (requestDto.getRequesterId() == requestDto.getReceiverId()) {
            throw new IllegalArgumentException("User can't request mentoring from yourself");
        }
        MentorshipRequest request = mentorshipRequestService.requestMentorship(requestMapper.toEntity(requestDto));
        return requestMapper.toDto(request);
    }

    @PostMapping("/getRequests")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return requestMapper.toEntityList(mentorshipRequestService.getRequests(filter));
    }

    @PutMapping("/{id}/accept")
    public void acceptRequest(@PathVariable("id") long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
    }

    @PutMapping("/{id}/reject")
    public void rejectRequest(@PathVariable("id") long requestId, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}
