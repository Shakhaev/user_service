package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventExistsException;
import school.faang.user_service.exception.EventParticipationRegistrationException;
import school.faang.user_service.exception.UserExistsException;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
public class EventValidator {
    private EventParticipationRepository eventParticipationRepository;

    public boolean validateParticipationRegistered(long eventId, long userId) {
        User participant = eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId);
        if (participant != null) {
            throw new EventParticipationRegistrationException("User %d already registered on event %d".formatted(userId, eventId));
        } else {
            return false;
        }
    }

    public boolean validateParticipationNotRegistered(long eventId, long userId) {
        User participant = eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId);
        if (participant == null) {
            throw new EventParticipationRegistrationException("User %d does not registered on event %d".formatted(userId, eventId));
        }
        return false;
    }

    public boolean checkEventExists(long eventId) {
        if (!eventParticipationRepository.eventExistsById(eventId)) {
            throw new EventExistsException("Event %d does not exist".formatted(eventId));
        }
        return true;
    }

    public boolean checkUserExists(long userId) {
        if (!eventParticipationRepository.userExistsById(userId)) {
            throw new UserExistsException("User %d does not exist".formatted(userId));
        }
        return true;
    }
}
