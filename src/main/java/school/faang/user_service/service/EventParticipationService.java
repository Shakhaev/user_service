package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Transactional
    public ResponseEntity<Object> registerParticipant(long eventId, long userId) {
        ResponseEntity<Object> response;
        boolean isParticipantFound = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
        if (isParticipantFound) {
            response =  new ResponseEntity<>("Participant already exists", HttpStatus.CONFLICT);
        }
        else {
            try {
                eventParticipationRepository.register(eventId, userId);
                response = new ResponseEntity<>("success", HttpStatus.CREATED);
            }
            catch (Exception e) {
                response = new ResponseEntity<>("something went wrong:(", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
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
