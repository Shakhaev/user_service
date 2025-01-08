package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;


@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipantSuccess() {
        long eventId = 1L;
        long userId = 100L;
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void testRegisterParticipantAlreadyRegistered() {
        long eventId = 1L;
        long userId = 100L;
        User user = new User();
        user.setId(userId);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));
        Assert.assertThrows(IllegalStateException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId));
        Mockito.verify(eventParticipationRepository, Mockito.times(0))
                .register(eventId, userId);
    }

    @Test
    void testGetParticipants() {
        long eventId = 1L;
        eventParticipationService.getParticipants(eventId);
        Mockito.verify(eventParticipationRepository,
                Mockito.times(1)).findAllParticipantsByEventId(eventId);
    }

    @Test
    public void testGetParticipantsCount() {
        long eventId = 1L;
        eventParticipationService.getParticipantsCount(eventId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .countParticipants(eventId);
    }
}
