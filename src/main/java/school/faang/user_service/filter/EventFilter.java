package school.faang.user_service.filter;

import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

public interface EventFilter extends Filter<Event, EventFilterDto> {
}
