package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.EventParticipantsMapper;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;


@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventParticipationController {

    public final EventParticipationService eventParticipationService;
    public final EventParticipantsMapper eventParticipantsMapper;

    @PostMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Void> registerParticipant(
            @PathVariable @Positive(message = "Event ID must be a positive number.") long eventId,
            @PathVariable @Positive(message = "User ID must be a positive number.") long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Void> unregisterParticipant(
            @PathVariable @Positive(message = "Event ID must be a positive number.") long eventId,
            @PathVariable @Positive(message = "User ID must be a positive number.") long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<UserDto>> getParticipant(
            @PathVariable @Positive(message = "Event ID must be a positive number.") long eventId) {
        return ResponseEntity.ok(eventParticipationService.getParticipants(eventId).stream()
                .map(eventParticipantsMapper::toDto)
                .toList());
    }

    @GetMapping("/{eventId}/participants/count")
    public ResponseEntity<Integer> getParticipantsCount(
            @PathVariable @Positive(message = "Event ID must be a positive number.") long eventId) {
        return ResponseEntity.ok(eventParticipationService.getParticipantsCount(eventId));
    }
}
