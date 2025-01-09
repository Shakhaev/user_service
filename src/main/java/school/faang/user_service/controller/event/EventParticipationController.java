package school.faang.user_service.controller.event;

import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Component
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @Autowired
    public EventParticipationController(EventParticipationService eventParticipationService) {
        this.eventParticipationService = eventParticipationService;
    }

    public void registerParticipant(long userId, long eventId) {
        eventParticipationService.registerParticipant(userId, eventId);
    }


    public void unregisterParticipant(long userId, long eventId) {
        eventParticipationService.unregisterParticipant(userId, eventId);
    }

    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationService.getParticipant(eventId)
                .stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
