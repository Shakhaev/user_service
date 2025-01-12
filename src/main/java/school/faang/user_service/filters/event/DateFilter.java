package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Component
public class DateFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventDto filters) {
        return filters.getStartDate() != null;
    }

    @Override
    public boolean filterEntity(Event event, EventDto filters) {
        return filters.getStartDate().isBefore(event.getStartDate())
                && filters.getEndDate().isAfter(event.getEndDate());
    }
}
