package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        boolean isParticipantFound = eventParticipationRepository.findAllParticipantsByEventId(eventId).stream().anyMatch((user) -> user.getId() == userId);
        if (isParticipantFound) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "participant already exists");
        }
        else {
            eventParticipationRepository.register(eventId, userId);
        }
    }

    @Transactional
    public void unregister(long eventId, long userId) {
        boolean isParticipantNotFound = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .allMatch(user -> user.getId() != userId);
        if (isParticipantNotFound) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "participant not found");
        }
        else {
            eventParticipationRepository.unregister(eventId, userId);
        }
    }

    @Transactional
    public List<UserDto> getParticipant(long eventId) {
        List<User> participantList = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return participantList.stream().map(userMapper::toDto).toList();
    }

    @Transactional
    public int getParticipantsCount(long eventId) {
        int participantsCount = eventParticipationRepository.countParticipants(eventId);
        return participantsCount;
    }
}
