package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

public class OwnerFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventDto filters) {
        return false;
    }

    @Override
    public boolean filterEntity(Event event, EventDto filters) {
        return false;
    }
}
