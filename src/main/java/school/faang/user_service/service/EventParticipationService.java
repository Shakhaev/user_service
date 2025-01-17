package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipation(long eventId, long userId) {
        boolean isPatricipationRegistred = isPatricipantnRegistred(eventId, userId);
        if (isPatricipationRegistred) {
            throw new IllegalArgumentException(String.format("Пользователь с id: <%d>"
                    + "уже зарегистрирован на событие c id: <%d>", userId, eventId));
        }
        eventParticipationRepository.register(eventId, userId);
        log.info(String.format("Регистрация позьзователя с id: <%d>"
                + "на событие с id: <%d> -прошла успешно!", userId, eventId));

    }

    public void unregisterPartipation(long eventId, long userId) {
        boolean isPatricipantRegistred = isPatricipantnRegistred(eventId, userId);

        if (!isPatricipantRegistred) {
            throw new IllegalArgumentException(String.format("Пользователь с id: <%d>"
                    + "НЕ ЗАРЕГИСТРИРОВАН на событие с id: <%d>", userId, eventId));
        }
        eventParticipationRepository.unregister(eventId, userId);
        log.info("Отмена регистрации на событие с id: <%d>,"
                + "для пользователя с id: <%d> -прошла успешно!", eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> events = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        return events.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    public boolean isPatricipantnRegistred(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }

}
