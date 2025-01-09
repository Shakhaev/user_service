package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService participationService;


    public void registerParticipant(long userId, long eventId) {
        participationService.registerParticipant(userId, eventId);
    }

    public void unregisterParticipant(long userId, long eventId) {
        participationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        return participationService.getParticipant(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return participationService.getParticipantsCount(eventId);
    }

}
