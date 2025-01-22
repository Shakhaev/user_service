package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Spy
    private UserMapperImpl userMapper;

    @Test
    void testRegisterParticipantWhenItIsAlreadyIn() {
        long userId = 1L;
        long eventId = 2L;
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of(User.builder()
                        .id(userId)
                        .build()));
        assertNotEquals(
            HttpStatus.CONFLICT,
            eventParticipationService.registerParticipant(userId, eventId).getStatusCode()
        );
    }

    @Test
    void testRegisterParticipantInvocation() {
        long userId = 1L;
        long eventId = 2L;
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of());
        eventParticipationService.registerParticipant(eventId, userId);
        verify(
                eventParticipationRepository,
                times(1)
        ).register(eventId, userId);
    }

    @Test
    void testUnregisterParticipantWhenUserNotInEvent() {
        long userId = 1L;
        long eventId = 2L;
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of());
        assertThrows(
                ResponseStatusException.class,
                () -> eventParticipationService.unregister(eventId, userId)
        );
    }

    @Test
    void testUnregisterParticipantInvocation() {
        long userId = 1L;
        long eventId = 2L;
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of(User.builder()
                        .id(userId)
                        .build()));
        eventParticipationService.unregister(eventId, userId);
        verify(
                eventParticipationRepository,
                times(1)
        ).unregister(eventId, userId);
    }


    @Test
    void testGetRegisterParticipantListIfException() {
        long eventId = 1L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenThrow(new RuntimeException("Exception"));
        assertThrows(
                RuntimeException.class,
                () -> eventParticipationService.getParticipant(eventId)
        );
    }

    @Test
    void testGetRegisterParticipantListInvocation() {
        long eventId = 2L;
        long userId = 1L;
        when(eventParticipationRepository.findAllParticipantsByEventId(anyLong()))
                .thenReturn(List.of(User.builder().id(userId).build()));
        List<UserDto> users = eventParticipationService.getParticipant(eventId);
        verify(
                eventParticipationRepository,
                times(1)
        ).findAllParticipantsByEventId(eventId);
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getId(), userId);

    }

    @Test
    void testGetRegisterParticipantCountIfException() {
        long eventId = 1L;
        when(eventParticipationRepository.countParticipants(anyLong()))
                .thenThrow(new RuntimeException("Exception"));
        assertThrows(
                RuntimeException.class,
                () -> eventParticipationService.getParticipantsCount(eventId)
        );
    }

    @Test
    void testGetRegisterParticipantCountInvocation() {
        long eventId = 2L;
        when(eventParticipationRepository.countParticipants(anyLong()))
                .thenReturn(1);
        int participantsCount = eventParticipationService.getParticipantsCount(eventId);
        verify(
                eventParticipationRepository,
                times(1)
        ).countParticipants(eventId);
        assertEquals(participantsCount, 1);
    }
}