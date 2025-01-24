package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        if (isAlreadyRegistered(eventId, userId)) {
            throw new RuntimeException("User is already registered for the event.");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!isAlreadyRegistered(eventId, userId)) {
            throw new RuntimeException("User wasn't registered on the event yet");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(user -> userMapper.toDto(user))
                .toList();
    }

    private boolean isAlreadyRegistered(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
