package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserAlreadyRegisteredException;
import school.faang.user_service.exception.user.UserWasNotRegisteredException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventRepository;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (isRegistered(eventId, userId)) {
            throw new UserAlreadyRegisteredException(userId);
        }
        eventRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        if (!isRegistered(eventId, userId)) {
            throw new UserWasNotRegisteredException(userId);
        }
        eventRepository.unregister(eventId, userId);
    }

    private boolean isRegistered(long eventId, long userId) {
        return !eventRepository.findAllParticipantsByEventAndUserId(eventId, userId).isEmpty();
    }

    @Transactional(readOnly = true)
    public List<User> getParticipants(long eventId) {
        return eventRepository.findAllParticipantsByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public int getParticipantsCount(long eventId) {
        return eventRepository.countParticipants(eventId);
    }
}
