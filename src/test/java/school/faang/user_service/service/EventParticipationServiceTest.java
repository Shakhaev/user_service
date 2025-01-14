package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Spy
    private UserMapperImpl userMapper;

    @Test
    public void testRegisterWithRegisteredUser() {
        long eventId = 1;
        long userId = 1;
        User existingUser = new User();
        existingUser.setId(userId);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(existingUser));

        eventParticipationRepository.findAllParticipantsByEventId(eventId);

        assertThrows(IllegalArgumentException.class, () -> eventService
                .registerParticipant(eventId, userId));
    }

    @Test
    public void testRegistrationUser() {
        long eventId = 1L;
        long userId = 1L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of());

        eventParticipationRepository.findAllParticipantsByEventId(eventId);
        eventService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1))
                .register(eventId, userId);
    }

    @Test
    public void testUnregisterWithUnregisteredUser() {
        long eventId = 1L;
        long userId = 1L;
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());

        eventParticipationRepository.findAllParticipantsByEventId(eventId);

        assertThrows(IllegalArgumentException.class, () -> eventService
                .unregisterParticipant(eventId, userId));
    }

    @Test
    public void testUnregisterUser() {
        long eventId = 1L;
        long userID = 1L;
        User existingUser = new User();
        existingUser.setId(userID);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(existingUser));

        eventParticipationRepository.findAllParticipantsByEventId(eventId);
        eventService.unregisterParticipant(eventId, userID);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userID);


    }

    @Test
    public void testGetParticipantUsers() {
        long eventId = 1L;
        User userFirst = new User();
        userFirst.setId(1L);
        userFirst.setUsername("John");
        userFirst.setEmail("John@gmail.com");
        User userSecond = new User();
        userSecond.setId(1L);
        userSecond.setUsername("John");
        userSecond.setEmail("John@gmail.com");
        List<User> users = List.of(userFirst, userSecond);
        List<UserDto> userDtos = List.of(
                new UserDto(1L, "John", "Doe"),
                new UserDto(2L, "Jane", "Smith")
        );
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);
        when(userMapper.usersToUserDtos(users)).thenReturn(userDtos);

        eventParticipationRepository.findAllParticipantsByEventId(eventId);
        userMapper.usersToUserDtos(users);
        verify(eventParticipationRepository, times(1))
                .findAllParticipantsByEventId(eventId);
    }

    @Test
    public void testgetParticipantsCount() {
        long eventId = 1L;
        int expectedCount = 10;
        when(eventParticipationRepository
                .countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationRepository.countParticipants(eventId);

        assertEquals(expectedCount, actualCount);
        verify(eventParticipationRepository, times(1))
                .countParticipants(1L);
    }

}
