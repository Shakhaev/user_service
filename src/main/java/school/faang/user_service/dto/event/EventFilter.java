package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filter);

    List<Event> apply(List<Event> events, EventFilterDto filterDto);
}