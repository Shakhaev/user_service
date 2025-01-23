package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.Event;

import java.util.List;

public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getLocation() != null;
    }

    @Override
    public List<Event> apply(List<Event> events, EventFilterDto filterDto) {
        return events.stream()
                .filter(f -> filterDto.getLocation().equals(f.getLocation()))
                .toList();
    }
}