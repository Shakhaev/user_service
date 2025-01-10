package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class EventStartDateFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getStartDate() != null;
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto filters) {
        events.filter(e -> e.getStartDate().toLocalDate().equals(filters.getStartDate().toLocalDate()));
    }
}
