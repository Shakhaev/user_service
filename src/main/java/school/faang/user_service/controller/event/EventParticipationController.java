package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.event.EventParticipationService;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long eventId, long userId) {
        try {
            eventParticipationService.registerParticipant(eventId, userId);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        try {
            eventParticipationService.unregisterParticipant(eventId, userId);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void getParticipant(long eventId) {
        eventParticipationService.getParticipant(eventId);
    }

    public void getParticipantsCount(long eventId) {
        eventParticipationService.getParticipantsCount(eventId);
    }
}
