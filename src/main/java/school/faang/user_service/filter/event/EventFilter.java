package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {

    boolean isApplicable(EventFilterDto filter);

    Stream<Event> apply(EventFilterDto filter, Stream<Event> events);
}
