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
        EventDto eventDto = EventDto.builder()
                .title("  ")
                .build();
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateEventWithBlankOwnerId() {
        EventDto eventDto = EventDto.builder()
                .title("  ")
                .ownerId(null)
                .build();
        ;
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateEventWithBlankStartDate() {
        EventDto eventDto = EventDto.builder()
                .title(" ")
                .ownerId(null)
                .startDate(null)
                .build();
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

}
