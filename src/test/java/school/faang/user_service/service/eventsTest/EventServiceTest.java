package school.faang.user_service.service.eventsTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventDto eventDto;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    @Spy
    private EventMapper eventMapper;

    @Captor
    private ArgumentCaptor<EventDto> eventCaptor;

    @Test
    public void testPrepareEventCandidate() {
        EventDto newEventDtoBefore = createNewEventCandidate();
        eventMapper.toEntityEvent(newEventDtoBefore);
        verify(eventMapper, times(1)).toEntityEvent(eventCaptor.capture());
        EventDto newEventDtoAfter = eventCaptor.getValue();
        assertEquals(newEventDtoBefore, newEventDtoAfter);
    }

    @Test
    public void testCreateEventCandidate() {


    }

    private EventDto createNewEventCandidate() {
        return EventDto.builder()
                .id(1L)
                .title("title")
                .ownerId(2L)
                .relatedSkills(List.of(3L,4L))
                .build();
    }
}
