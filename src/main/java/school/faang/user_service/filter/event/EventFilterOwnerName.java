package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
public class EventFilterOwnerName implements EventFilter {
    @Override
    public boolean isApplicable(EventFilters eventFilters) {
        return eventFilters.ownerName() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilters eventFilters) {
        String ownerNameFromFilter = eventFilters.ownerName()
                .trim()
                .toLowerCase();
        return events.filter(event -> event.getOwner().getUsername().equalsIgnoreCase(ownerNameFromFilter));
    }
}
