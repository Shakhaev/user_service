package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
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

    @Test
    void shouldNotSaveWhenEventNotValid() {
        prepareValidationThrows();

        Assertions.assertThrows(DataValidationException.class, () -> eventService.create(new EventCreateDto()));
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test
    void shouldSaveWhenEventValid() {
        EventCreateDto preparedEvent = new EventCreateDto();

        Assertions.assertDoesNotThrow(() -> eventService.create(preparedEvent));
        Mockito.verify(eventRepository, Mockito.times(1))
                .save(eventMapper.fromCreateDtoToEntity(preparedEvent));
    }

    @Test
    void shouldThrowWhenNoEventWithId() {
        prepareFindingEvent(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(0L));
    }

    @Test
    void shouldNotThrowWhenIdExists() {
        prepareFindingEvent(true);
        Assertions.assertDoesNotThrow(() -> eventService.getEvent(0L));
    }

    @Test
    void shouldReturnEventsByFilter() {
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
                .map(eventMapper::toForClientDto)
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
    void shouldThrowWithInvalidUpdateInfo() {
        prepareValidationThrows();

        Assertions.assertThrows(DataValidationException.class, () -> eventService.create(new EventCreateDto()));
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test
    void shouldThrowWhenUpdateOwnerNotFound() {
        prepareFindingEvent(false);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> eventService.updateEvent(new EventUpdateDto()));
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test
    void shouldSaveUpdatedEvent() {
        prepareFindingEvent(true);

        Assertions.assertDoesNotThrow(() -> eventService.updateEvent(new EventUpdateDto()));
        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.any(Event.class));
    }

    private void prepareValidationThrows() {
        Mockito.doThrow(new DataValidationException("Test simulated exception"))
                .when(eventValidator).validateEventInfo(Mockito.any(Event.class));
    }

    private void prepareFindingEvent(boolean entityExist) {
        if (entityExist) {
            Mockito.when(eventRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(new Event()));
        } else {
            Mockito.when(eventRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.empty());
        }
    }
}
