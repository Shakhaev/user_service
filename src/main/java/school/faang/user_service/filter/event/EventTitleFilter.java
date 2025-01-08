package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilters eventFilters) {
        return eventFilters.title() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilters eventFilters) {
        String filterTitle = eventFilters.title()
                .trim()
                .toLowerCase();
        return events.filter(event -> event.getTitle()
                .toLowerCase()
                .contains(filterTitle));
    }
}
