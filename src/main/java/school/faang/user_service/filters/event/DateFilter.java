package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

@Component
public class DateFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventDto filters) {
        return false;
    }

    @Override
    public boolean filterEntity(Event event, EventDto filters) {
        return false;
    }
}
