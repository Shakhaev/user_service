package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;


    @Test
    public void registerParticipant_SuccessfulRegisteredIfUserIsNotRegisteredBefore() {
        long eventId = 1L;
        long userId = 100L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        eventParticipationService.registerParticipant(eventId,userId);

        verify(eventParticipationRepository,times(1)).register(eventId,userId);
    }

    @Test
    public void registerParticipant_ParticipantCantBeRegisteredBecauseHeAlreadyRegistered(){
        long eventId = 1L;
        long userId = 100L;
        User user = new User();
        user.setId(userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));

        assertThrows(RuntimeException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));

    }

    @Test
    public void unregisterParticipant_ParticipantCantBeUnregisteredBecauseThisUserWasntRegistered(){
        long eventId = 1L;
        long userId = 100L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        assertThrows(RuntimeException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    public void unregisterParticipant_ParticipantIsSuccessfulUnregistered(){
        long eventId = 1L;
        long userId = 100L;
        User user = new User();
        user.setId(userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(eventId,userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId,userId);
    }

    @Test
    public void getParticipant_Successful () {
        long eventId = 1L;
        User firstTstUser = new User();
        User secondTestUser = new User();
        firstTstUser.setId(100L);
        secondTestUser.setId(101L);
        List<User> expectedList = List.of(firstTstUser,secondTestUser);
        when(eventParticipationService.getParticipant(eventId)).thenReturn(expectedList);

        List<User> actualList = eventParticipationService.getParticipant(eventId);

        assertEquals(expectedList, actualList);
    }

}
