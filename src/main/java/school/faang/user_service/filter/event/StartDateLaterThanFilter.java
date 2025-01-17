package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StartDateLaterThanFilter implements EventFilter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean isApplicable(EventFilterDto filter) {
        return filter.startDateLaterThan() != null;
    }

    public boolean test(EventFilterDto filter, Event event) {
        return event.getStartDate().isAfter(LocalDateTime.parse(filter.startDateLaterThan(), formatter));
    }
}
