package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
public class EventStatusFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        String eventStatus = filter.getEventStatusPattern();
        return eventStatus != null && !eventStatus.isBlank();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getStatus().getMessage()
                .equalsIgnoreCase(filter.getEventStatusPattern()));
    }
}
