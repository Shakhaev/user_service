package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class MaxAttendeesLessThanFilter implements EventFilter {
    public boolean isApplicable(EventFilterDto filter) {
        return filter.maxAttendeesLessThan() != null;
    }

    public boolean test(EventFilterDto filter, Event event) {
        return event.getMaxAttendees() < filter.maxAttendeesLessThan();
    }
}
