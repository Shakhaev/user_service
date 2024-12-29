package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class EventDateFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getDateTime() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filterDto) {
        return events
                .filter(event -> event.getEndDate().isAfter(filterDto.getDateTime()));
    }
}
