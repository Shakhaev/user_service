package school.faang.user_service.service.eventsTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @Spy
    private EventMapper eventMapper;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventController eventController;

    @Test
    public void testCreateEventWithBlankTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(" ");
        assertThrows(DataValidationException.class,()->eventController.create(eventDto));
    }

    @Test
    public void testCreateEventWithBlankOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(" ");
        eventDto.setOwnerId(null);;
        assertThrows(DataValidationException.class,()->eventController.create(eventDto));
    }

    @Test
    public void testCreateEventWithBlankStartDate() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(" ");
        eventDto.setOwnerId(null);;
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class,()->eventController.create(eventDto));
    }

}
