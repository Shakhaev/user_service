package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class EventTitleContainingFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        return events.filter(e -> e.getTitle().toLowerCase()
                .contains(filters.getTitle().toLowerCase()));
    }
}
