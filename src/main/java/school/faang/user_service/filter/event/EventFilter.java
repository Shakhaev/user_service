package school.faang.user_service.filter.event;

import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Filter;

public interface EventFilter extends Filter<Event, EventFilterDto> {
}
