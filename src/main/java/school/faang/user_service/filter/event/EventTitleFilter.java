package school.faang.user_service.filter.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFiltersDto eventFiltersDto) {
        return eventFiltersDto.title() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFiltersDto eventFiltersDto) {
        log.info("Filtering by title: {}", eventFiltersDto.title());
        return events.filter(event -> Optional.ofNullable(event.getTitle())
                .orElse("")
                .toLowerCase()
                .contains(eventFiltersDto.title()
                        .trim()
                        .toLowerCase()));
    }
}
