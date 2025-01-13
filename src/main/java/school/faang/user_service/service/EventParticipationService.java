package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long userId, long eventId) {
        boolean isUserRegistered = isUserRegistered(userId, eventId);

        if (isUserRegistered) {
            throw new BusinessException(String.format("Пользователь с id: <%d> уже "
                    + "зарегистрирован на событие c id: <%d>", userId, eventId));
        }

        eventParticipationRepository.register(eventId, userId);
        log.info(String.format("Регистрация пользователя с id: <%d> "
                + "на событие с id: <%d> - прошла успешно!", userId, eventId));
    }

    public void unregisterParticipant(long eventId, long userId) {
        boolean isUserRegistered = isUserRegistered(userId, eventId);

        if (!isUserRegistered) {
            throw new EntityNotFoundException(String.format("Пользователь с id: <%d> "
                    + "НЕ ЗАРЕГИСТРИРОВАН на событие c id: <%d>", userId, eventId));
        }

        eventParticipationRepository.unregister(eventId, userId);
        log.info(String.format("Отмена регистрации на событие с id: <%d> "
                + "для пользователя с id: <%d> - прошла успешно!", eventId, userId));
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> events = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        return events.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isUserRegistered(long userId, long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(user -> user.getId() == userId);
    }
}
