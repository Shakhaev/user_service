package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void registerParticipant_SuccessfulRegisteredIfUserIsNotRegisteredBefore() {
        long eventId = 1L;
        long userId = 100L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    public void registerParticipant_ParticipantCantBeRegisteredBecauseHeAlreadyRegistered() {
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
    public void unregisterParticipant_ParticipantCantBeUnregisteredBecauseThisUserWasntRegistered() {
        long eventId = 1L;
        long userId = 100L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        assertThrows(RuntimeException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    public void unregisterParticipant_ParticipantIsSuccessfulUnregistered() {
        long eventId = 1L;
        long userId = 100L;
        User user = new User();
        user.setId(userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    public void getParticipant_Successful() {
        long eventId = 1L;
        User firstTestUser = new User();
        firstTestUser.setId(1L);
        firstTestUser.setUsername("username1");
        firstTestUser.setEmail("email1@example.com");
        User secondTestUser = new User();
        secondTestUser.setId(2L);
        secondTestUser.setUsername("username2");
        secondTestUser.setEmail("email2@example.com");
        List<User> users = List.of(firstTestUser, secondTestUser);
        List<UserDto> expectedList = users.stream()
                .map(userMapper::toDto)
                .toList();

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);
        List<UserDto> actualList = eventParticipationService.getParticipant(eventId);

        assertEquals(expectedList, actualList);
    }


    @Test
    public void getParticipantsCount() {
        long eventId = 1L;
        int expectedCount = 10;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }

}
