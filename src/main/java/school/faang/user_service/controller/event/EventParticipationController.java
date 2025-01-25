package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.participant.RegisterParticipantDto;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequestMapping("/participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;
    private  RegisterParticipantDto registerParticipantDto;

//    private RegisterParticipantDto  validateParticipant(RegisterParticipantDto registerParticipantDto) {
//        long eventId = registerParticipantDto.getEventId();
//        long userId = registerParticipantDto.getUserId();
//
//        return registerParticipantDto.getEventId() == eventId && registerParticipantDto.getUserId() == userId ? registerParticipantDto : null;
//    }

    @PostMapping("/{userId}/{eventId}")
    public void registerParticipant(@RequestParam RegisterParticipantDto registerParticipantDto) {
        long userId = registerParticipantDto.getUserId();
        long eventId = registerParticipantDto.getEventId();

        eventParticipationService.registerParticipation(eventId, userId);
    }

    @DeleteMapping("/{userId}/{eventId}")
    public void unregisterParticipant(@RequestParam RegisterParticipantDto registerParticipantDto) {
        long userId = registerParticipantDto.getUserId();
        long eventId = registerParticipantDto.getEventId();

        eventParticipationService.unregisterParticipation(eventId, userId);
    }


    public void getParticipant(@PathVariable long userId, @PathVariable long eventId) {
        
    }
}
