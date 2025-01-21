package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.Objects;

public class OwnerFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters != null;
    }

    @Override
    public boolean filterEntity(Event event, EventFilterDto filters) {
       return event.getOwner().getId() == filters.getOwnerId();
    }
}
