package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class EventStatusFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEventStatus() != null && Arrays.asList(EventStatus.values()).contains(filter.getEventStatus());
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto filter) {
        events.filter(event -> event.getStatus().getMessage().contains(filter.getEventStatus().getMessage()));
    }
}
