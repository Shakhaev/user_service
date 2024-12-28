package school.faang.user_service.service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import java.util.stream.Stream;

@Component
public class EventIdFilter implements Filter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.id() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> event, EventFilterDto filter) {
        return event.filter(event1 -> Long.valueOf(event1.getId()).equals(filter.id()));
    }
}