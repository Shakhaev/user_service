package school.faang.user_service.controller;

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
@RequestMapping
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Описание запроса на менторство не может быть пустым.");
        }
        MentorshipRequestDto response = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
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

    @PostMapping()
    public ResponseEntity<String> acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
        return ResponseEntity.ok("Запрос на менторство успешно принят.");
    }

    @PostMapping()
    public ResponseEntity<String> rejectRequest(
            @PathVariable long id,
            @RequestBody RejectionDto rejection
    ) {
        mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok("Запрос на менторство успешно отклонен.");
    }
}
