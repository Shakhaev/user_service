package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
public class LocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        String location = filter.getLocationPattern();
        return location != null && !location.isBlank();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getLocation().toLowerCase()
                .contains(filter.getLocationPattern().toLowerCase()));
    }
}
