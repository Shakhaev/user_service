package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
public class OwnerFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        String owner = filter.getOwnerPattern();
        return owner != null && !owner.isBlank();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getOwner().getUsername().toLowerCase()
                .contains(filter.getOwnerPattern()));
    }
}
