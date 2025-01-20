package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;


    public void registerParticipant(long eventId, long userId) {

        if (isParticipantRegistered(eventId, userId)) {
            throw new IllegalArgumentException("The user is already a participant in the event! ");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {

        if (!isParticipantRegistered(eventId, userId)) {
            throw new IllegalArgumentException("The user was not found in this event! ");
        }
        eventParticipationRepository
                .unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.usersToUserDtos(users);
    }

    public int getParticipantsCount(long eventId) {

        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isParticipantRegistered(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
