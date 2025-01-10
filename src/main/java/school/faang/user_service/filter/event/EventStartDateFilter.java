package school.faang.user_service.filter.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
@Slf4j
public class EventStartDateFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFiltersDto eventFiltersDto) {
        return eventFiltersDto.startDate() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFiltersDto eventFiltersDto) {
        log.info("Filtering by start date: {}", eventFiltersDto.startDate());
        return events.filter(event -> !event.getStartDate().isBefore(eventFiltersDto.startDate()));
    }
}
