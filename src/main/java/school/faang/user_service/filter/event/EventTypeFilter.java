package school.faang.user_service.filter.event;

import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public enum EventTypeFilter implements EventFilter {
    TYPE;

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEventTypePattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getType() == filter
                .getEventTypePattern());
    }
}
