package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/api/mentorship/requests")
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequestDto response = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/mentorship/filters")
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long requesterId,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) RequestStatus status
    ) {

        RequestFilterDto filters = new RequestFilterDto();
        filters.setDescription(description);
        filters.setRequesterId(requesterId);
        filters.setReceiverId(receiverId);
        filters.setStatus(status);

        List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(filters);

        return ResponseEntity.ok(requests);
    }

    @PutMapping("/api/mentorship/{id}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
        return ResponseEntity.ok("Запрос на менторство успешно принят.");
    }

    @PutMapping("/api/mentorship/{id}/reject")
    public ResponseEntity<String> rejectRequest(
            @PathVariable long id,
            @Valid @RequestBody RejectionDto rejection
    ) {
        mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok("Запрос на менторство успешно отклонен.");
    }
}
