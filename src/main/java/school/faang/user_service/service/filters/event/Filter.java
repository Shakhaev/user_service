package school.faang.user_service.service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public interface Filter {
    boolean isApplicable(EventFilterDto filter);

    Stream<Event> apply(Stream<Event> event, EventFilterDto filter);
}