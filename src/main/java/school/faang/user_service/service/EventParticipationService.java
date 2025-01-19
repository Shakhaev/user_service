package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UnRegistredException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto registerParticipant(long eventId, long userId) {
        boolean isPatricipantRegistred = isPatricipantnRegistred(eventId, userId);
        if (isPatricipantRegistred) {
            throw new UserNotFoundException("Пользователь уже зарегистрирован!");
        }
        eventParticipationRepository.register(eventId, userId);
        log.info("Регистрация позьзователя с id: {} на событие с id: {} -прошла успешно!", userId, eventId);

        User user = userRepository.getReferenceById(userId);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto unregisterParticipant(long eventId, long userId) {
        boolean isPatricipantRegistred = isPatricipantnRegistred(eventId, userId);

        if (!isPatricipantRegistred) {
            throw new UnRegistredException("Пользователь не ЗАРЕГИСТРИРОВАН на событие!");
        }
        eventParticipationRepository.unregister(eventId, userId);
        log.info("Отмена регистрации на событие с id: {} для пользователя с id: {} -прошла успешно!", eventId, userId);

        User user = userRepository.getReferenceById(userId);
        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> events = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        return Optional.ofNullable(events)
                .orElseGet(ArrayList::new)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    public boolean isPatricipantnRegistred(long eventId, long userId) {
        Optional<List<User>> participants = Optional.ofNullable(eventParticipationRepository.findAllParticipantsByEventId(eventId));

        return participants
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .anyMatch(user -> user.getId() == userId))
                .orElse(false);
    }

}
