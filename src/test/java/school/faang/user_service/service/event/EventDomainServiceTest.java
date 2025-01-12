package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.exceptions.EventNotFoundException;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventDomainServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventDomainService eventDomainService;

    private final Event event = mock(Event.class);

    @Test
    void testSave_successful() {
        when(eventRepository.save(event)).thenReturn(event);

        assertThat(eventDomainService.save(event))
                .isInstanceOf(Event.class);
    }

    @Test
    void testDelete_successful() {
        eventDomainService.delete(event);

        verify(eventRepository).delete(event);
    }

    @Test
    void testFindById_successful() {
        long id = 1L;

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        assertThat(eventDomainService.findById(id))
                .isInstanceOf(Event.class);
    }

    @Test
    void testFindById_notFound_exception() {
        long id = 1L;

        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventDomainService.findById(id))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(new EventNotFoundException(id).getMessage());
    }

    @Test
    void testFindAllSortedByPromotedEventsPerPage_successful() {
        long offset = 0;
        long limit = 2L;
        List<Event> events = List.of(event);

        when(eventRepository.findAllSortedByPromotedEventsPerPage(offset, limit)).thenReturn(events);

        assertThat(eventDomainService.findAllSortedByPromotedEventsPerPage(offset, limit).get(0))
                .isInstanceOf(Event.class);
    }
}