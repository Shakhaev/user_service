package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    public UserReadDto registerParticipant(long userId, long eventId) {
        return eventParticipationService.registerParticipant(userId, eventId);
    }

    public UserReadDto unregisterParticipant(long userId, long eventId) {
        return eventParticipationService.unregisterParticipant(userId, eventId);
    }

    public List<UserReadDto> getParticipant(long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
