package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final UserService userService;
    private final EventServiceValidator validator;

    @Value("${scheduler.clear-events.chunk-size}")
    @Setter
    private int chunkSize;

    public EventDto create(EventDto eventDto) {
        User owner = userService.findUserById(eventDto.getOwnerId());
        Event event = eventMapper.toEntity(eventDto, userService);
        event.setOwner(owner);

        validator.validateRequiredSkills(owner, event);

        return eventMapper.toDto(eventRepository.save(event));
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found for ID: " + eventId)));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();

        return eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filters))
                .reduce(events,
                        ((eventStream, filter) -> filter.apply(eventStream, filters)),
                        ((eventStream, eventStream2) -> eventStream2)
                )
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        validator.validateEventId(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        User owner = userService.findUserById(eventDto.getOwnerId());
        Event event = eventMapper.toEntity(eventDto, userService);
        event.setOwner(owner);

        validator.validateRequiredSkills(owner, event);

        return eventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }


    public void clearPastEvents() {
        List<Event> events = eventRepository.findAll();
        List<Event> pastEvents = events.stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();

        List<List<Event>> partitions = partitionList(pastEvents, chunkSize);
        ExecutorService executor = Executors.newFixedThreadPool(partitions.size());

        for (List<Event> partition : partitions) {
            executor.submit(() -> eventRepository.deleteAllByIdInBatch(
                    partition.stream().map(Event::getId).toList()
            ));
        }
        executor.shutdown();
    }

    private <T> List<List<T>> partitionList(List<T> list, int size) {
        int numPartitions = (list.size() + size - 1) / size;
        return IntStream.range(0, numPartitions)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
                .toList();
    }

}