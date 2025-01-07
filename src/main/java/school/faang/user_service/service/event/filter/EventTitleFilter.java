package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getTitle() != null && !filter.getTitle().isEmpty();
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto filter) {
        events.filter(event -> event.getTitle().contains(filter.getTitle()));
    }
}
