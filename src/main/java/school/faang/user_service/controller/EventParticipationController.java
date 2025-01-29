package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    void register(@RequestParam long userId, @RequestParam long eventId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregister")
    void unregister(@RequestParam long userId, @RequestParam long eventId) {
        eventParticipationService.unregister(eventId, userId);
    }


    @GetMapping("/participants/{eventId}")
    List<UserDto> getParticipants(@PathVariable long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/participants/count/{eventId}")
    Integer getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
