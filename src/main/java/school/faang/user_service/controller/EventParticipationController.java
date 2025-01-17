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

    public void registerParticipation(long userId, long eventId) {
        eventParticipationService.registerParticipation(userId, eventId);
    }

    public void unregisterPartipantion(long userId, long eventId) {
        eventParticipationService.unregisterPartipation(userId, eventId);
    }

    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
