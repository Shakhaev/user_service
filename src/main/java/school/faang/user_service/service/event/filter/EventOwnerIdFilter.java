package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventOwnerIdFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getOwnerId() != null;
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto filter) {
        events.filter(event -> event.getOwner().getId().equals(filter.getOwnerId()));
    }
}
