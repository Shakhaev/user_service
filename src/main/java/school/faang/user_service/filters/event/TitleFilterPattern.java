package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

import school.faang.user_service.entity.event.Event;


@Component
public class TitleFilterPattern implements EventFilter {

    @Override
    public boolean isApplicable(EventDto filters) {
        return filters != null;
    }

    @Override
    public boolean filterEntity(Event event, EventDto filters) {
        return event.getTitle().matches(filters.getTitle());
    }
}
