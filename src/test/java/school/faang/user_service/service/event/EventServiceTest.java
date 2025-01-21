package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;

    @Captor
    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

    @Test
    void createEvent_ShouldNotSaveWhenEventNotValid() {
        prepareValidationThrows();

        Assertions.assertThrows(DataValidationException.class, () -> eventService.createEvent(new CreateEventDto()));
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test
    void createEvent_ShouldSaveWhenEventValid() {
        CreateEventDto preparedEvent = new CreateEventDto();

        Assertions.assertDoesNotThrow(() -> eventService.createEvent(preparedEvent));
        Mockito.verify(eventRepository, Mockito.times(1))
                .save(Mockito.any(Event.class));
    }

    @Test
    void getEvent_ShouldThrowWhenNoEventWithId() {
        prepareFindingEvent(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(0L));
    }

    @Test
    void getEvent_ShouldNotThrowWhenIdExists() {
        Event event = prepareFindingEvent(true).get();
        Assertions.assertEquals(eventMapper.toDto(event), eventService.getEvent(0L));
    }

    @Test
    void getEventsByFilter_ShouldReturnFilteredResults() {
        EventFilter filter2 = Mockito.mock(EventFilter.class);
        EventFilter filter1 = Mockito.mock(EventFilter.class);
        List<EventFilter> eventFilters = List.of(filter1, filter2);
        eventService = new EventService(eventRepository, eventValidator, eventMapper, eventFilters);

        var filterDto = new EventFilterDto();

        Event correctEvent1 = new Event();
        Event correctEvent2 = new Event();
        Event wrongEvent = new Event();
        var eventList = List.of(correctEvent1, correctEvent2, wrongEvent);

        var expectedRes = Stream.of(correctEvent1, correctEvent2)
                .map(eventMapper::toDto)
                .toList();

        Mockito.when(eventRepository.findAll()).thenReturn(eventList);
        Mockito.when(filter2.isApplicable(Mockito.any()))
                .thenReturn(true);
        Mockito.when(filter1.isApplicable(Mockito.any()))
                .thenReturn(true);
        Mockito.when(filter1.apply(Mockito.any(), Mockito.any()))
                .thenReturn(Stream.of(correctEvent1, correctEvent2, wrongEvent));
        Mockito.when(filter2.apply(Mockito.any(), Mockito.any()))
                .thenReturn(Stream.of(correctEvent1, correctEvent2));

        Assertions.assertEquals(expectedRes, eventService.getEventsByFilter(filterDto));
    }

    @Test
    void updateEvent_ShouldThrowWithInvalidUpdateInfo() {
        prepareValidationThrows();

        Assertions.assertThrows(DataValidationException.class, () -> eventService.updateEvent(new UpdateEventDto()));
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test
    void updateEvent_ShouldThrowWhenEventNotFound() {
        prepareFindingEvent(false);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> eventService.updateEvent(new UpdateEventDto()));
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test
    void updateEvent_ShouldSaveUpdatedEvent() {
        Event event = prepareFindingEvent(true).get();
        Mockito.when(eventRepository.save(eventCaptor.capture()))
                .thenAnswer(invocation -> eventCaptor.getValue());
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setTitle("some new title");
        updateEventDto.setId(event.getId());
        event.setTitle(updateEventDto.getTitle());
        Assertions.assertEquals(eventMapper.toDto(event), eventService.updateEvent(updateEventDto));
        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.any(Event.class));
    }

    @Test
    void getParticipatedEvents_ShouldReturnEventList() {
        Event event1 = new Event();
        Event event2 = new Event();
        event1.setId(1L);
        event2.setId(2L);

        Mockito.when(eventRepository.findParticipatedEventsByUserId(Mockito.anyLong()))
                .thenReturn(List.of(event1, event2));

        Assertions.assertEquals(List.of(eventMapper.toDto(event1), eventMapper.toDto(event2)),
                eventService.getParticipatedEvents(1L));
    }

    private void prepareValidationThrows() {
        Mockito.doThrow(new DataValidationException("Test simulated exception"))
                .when(eventValidator).validateEventInfo(Mockito.any(Event.class));
    }

    private Optional<Event> prepareFindingEvent(boolean entityExist) {
        if (entityExist) {
            Event event = new Event();
            event.setId(1L);
            event.setTitle("title");
            var optional = Optional.of(event);
            Mockito.when(eventRepository.findById(Mockito.anyLong()))
                    .thenReturn(optional);
            return optional;
        } else {
            Mockito.when(eventRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.empty());
            return Optional.empty();
        }
    }
}
