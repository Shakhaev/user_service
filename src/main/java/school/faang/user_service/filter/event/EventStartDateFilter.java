package school.faang.user_service.filter.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@Component
public class EventStartDateFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFiltersDto eventFiltersDto) {
        return eventFiltersDto.startDate() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFiltersDto eventFiltersDto) {
        LocalDateTime filterStartDate = eventFiltersDto.startDate();
        log.info("Filtering by start date: {}", filterStartDate);

        return events.filter(event -> {
            LocalDateTime eventStartDate = event.getStartDate();
            if (eventStartDate == null) {
                return false;
            }
            return eventStartDate.isAfter(filterStartDate) || eventStartDate.isEqual(filterStartDate);
        });
    }
}
