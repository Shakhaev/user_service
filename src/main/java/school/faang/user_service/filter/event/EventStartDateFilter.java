package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
public class EventStartDateFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilters eventFilters) {
        return eventFilters.startDate() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilters eventFilters) {
        return events.filter(event -> !event.getStartDate().isBefore(eventFilters.startDate()));
    }
}
