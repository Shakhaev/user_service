package school.faang.user_service.controller.event.event;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.faang.user_service.dto.event.RegisterParticipantDto;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequestMapping("/participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping
    public void registerParticipant(RegisterParticipantDto registerParticipantDto) {
        long eventId = registerParticipantDto.getEventId();
        long userId = registerParticipantDto.getUserId();

        eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping
    public void unregisterParticipant(RegisterParticipantDto registerParticipantDto) {
        long eventId = registerParticipantDto.getEventId();
        long userId = registerParticipantDto.getUserId();

        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping
    public void getParticipant(RegisterParticipantDto registerParticipantDto) {
        long eventId = registerParticipantDto.getEventId();
        eventParticipationService.getParticipant(eventId);
    }

    @GetMapping
    public void getParticipantCounts(RegisterParticipantDto registerParticipantDto) {
        long eventId = registerParticipantDto.getEventId();
        eventParticipationService.getParticipantCounts(eventId);
    }
}
