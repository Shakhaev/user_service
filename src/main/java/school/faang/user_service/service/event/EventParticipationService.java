package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;



    public void registerParticipant(long eventId, long userId) {
        boolean isAlreadyRegister = eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
        if (isAlreadyRegister) {
            throw new IllegalArgumentException("The user is already a participant in the event! ");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long userId, long eventId) {
        boolean isAlreadyUnregister = eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
        if (!isAlreadyUnregister) {
            throw new IllegalArgumentException("The user was not found in this event! ");
        }
        eventParticipationRepository
                .unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        List<UserDto> dto = userMapper.usersToUserDtos(users);
        return dto;
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
