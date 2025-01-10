package school.faang.user_service.filter;

import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFiltersDto eventFiltersDto);

    Stream<Event> apply(Stream<Event> events, EventFiltersDto eventFiltersDto);
}
