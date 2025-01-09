package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    @Autowired
    public EventParticipationService(EventParticipationRepository eventParticipationRepository) {
        this.eventParticipationRepository = eventParticipationRepository;
    }

    public void registerParticipant(long eventId, long userId) {
        boolean isAlreadyRegister = eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
        if (isAlreadyRegister) {
            throw new IllegalArgumentException("Пользователь уже является участником события! ");
        }
        registerParticipant(eventId, userId);

    }

    public void unregisterParticipant(long userId, long eventId) {
        boolean isAlreadyUnregister = eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
        if (!isAlreadyUnregister) {
            throw new IllegalArgumentException("Пользователь не был найден в данном событии! ");
        }
        eventParticipationRepository
                .unregister(eventId, userId);
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
