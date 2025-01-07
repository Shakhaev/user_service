package school.faang.user_service.service.eventsTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventDto eventDto;
    @Mock
    private EventRepository eventRepositorySpy;

    @InjectMocks
    private EventService eventService;

    @Spy
    private EventMapper eventMapper;


    @Test
    public void testCreateEventService() {


    }
}
