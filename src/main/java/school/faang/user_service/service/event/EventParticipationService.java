package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.RegisterParticipantMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final RegisterParticipantMapper registerParticipantMapper;

    @Transactional
    public void registerParticipation(long userId, long eventId) {
        List<UserDto> participant = registerParticipantMapper.toDtoList(eventParticipationRepository.findAllParticipantsByEventId(eventId));

        eventParticipationRepository.register(userId, eventId);
    }

    @Transactional
    public void unregisterParticipation(long userId, long eventId) {
        List<UserDto> participant = registerParticipantMapper.toDtoList(eventParticipationRepository.findAllParticipantsByEventId(eventId));

        eventParticipationRepository.unregister(userId, eventId);
    }
}