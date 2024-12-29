package school.faang.user_service.service;

import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.User;

import java.util.List;


public interface EventService {
     EventDto create(EventDto event);
     EventDto getEvent(long eventId);
     List<EventDto> getEventsByFilter(EventFilterDto filter);
     void deleteEvent(long id);
     EventDto updateEvent(EventDto event);
     List<EventDto>  getOwnedEvents(long userId);
     List<EventDto> getParticipatedEvents(long userId);
     User findUser(Long id);
}