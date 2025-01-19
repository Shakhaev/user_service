package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long userId, long eventId) {
        eventParticipationService.registerParticipant(userId, eventId);
    }

    public void unregisterParticipant(long userId, long eventId) {
        eventParticipationService.unregisterParticipant(userId, eventId);
    }

    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
