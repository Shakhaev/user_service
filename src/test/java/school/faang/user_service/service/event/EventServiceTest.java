package school.faang.user_service.service.event;

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
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private SkillMapperImpl skillMapper;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private List<EventFilter> eventFilters;
    @InjectMocks
    private EventService eventService;

    @Captor
    private ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

    @Test
    void shouldNotSaveWhenEventNotValid() {
        EventDto eventDto = prepareData(false);
        Assertions.assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
    }

    @Test
    void shouldSaveWhenEventValid() {
        EventDto preparedEvent = prepareData(true);
        Mockito.when(eventRepository.save(Mockito.any(Event.class)))
                .then(invocation -> invocation.<Event>getArgument(0));

        EventDto savedEvent = eventService.create(preparedEvent);

        Mockito.verify(eventRepository, Mockito.times(1))
                .save(Mockito.any(Event.class));

        Assertions.assertEquals(preparedEvent, savedEvent);
    }

    @Test
    void shouldThrowWhenNoEventWithId() {
        Mockito.when(eventRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(0L));
    }

    @Test
    void shouldNotThrowWhenNoEventWithId() {
        Mockito.when(eventRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Event()));
        Assertions.assertDoesNotThrow(() -> eventService.getEvent(0L));
    }

    @Test
    void shouldNotThrowWhenNoEventsInDatabase() {
        Mockito.when(eventRepository.findAll())
                .thenReturn(null);
        Assertions.assertDoesNotThrow(() -> eventService.getEventsByFilter(new EventFilterDto()));
    }

    private EventDto prepareData(boolean validationResult) {
        if (!validationResult) {
            Mockito.doThrow(new DataValidationException("Owner should have skills"))
                    .when(eventValidator).validateEventCreatorSkills(Mockito.any(EventDto.class));
        }
        EventDto eventDto = new EventDto();
        eventDto.setTitle("title");
        eventDto.setRelatedSkillsIds(List.of(10L, 11L, 12L));
        eventDto.setOwnerId(1L);
        eventDto.setStartDate(LocalDateTime.now().plusMonths(1));
        return eventDto;
    }
}
