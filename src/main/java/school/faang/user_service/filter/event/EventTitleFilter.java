package school.faang.user_service.filter.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.stream.Stream;

@Component
@Slf4j
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFiltersDto eventFiltersDto) {
        return eventFiltersDto.title() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFiltersDto eventFiltersDto) {
        String filterTitle = eventFiltersDto.title()
                .trim()
                .toLowerCase();
        log.info("Filtering by title: {}", filterTitle);
        return events.filter(event -> event.getTitle()
                .toLowerCase()
                .contains(filterTitle));
    }
}
