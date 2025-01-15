package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventRequestDto;

import java.util.List;

public interface EventService {
    List<EventDto> getEventsByFilter(EventFilterDto filter);

    EventDto getEvent(Long id);

    EventDto create(EventRequestDto eventData);

    EventDto update(EventRequestDto eventData, Long id);

    void deleteEvent(Long id);

    List<EventDto> getOwnedEvents(Long userId);

    List<EventDto> getParticipatedEvents(Long userId);
}
