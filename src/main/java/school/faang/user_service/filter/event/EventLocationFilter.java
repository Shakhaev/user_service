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
public class EventLocationFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFiltersDto eventFiltersDto) {
        return eventFiltersDto.location() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFiltersDto eventFiltersDto) {
        log.info("Filtering Event by location: {}", eventFiltersDto.location());
        return events.filter(event -> Optional.ofNullable(event.getLocation())
                .orElse("")
                .toLowerCase()
                .contains(eventFiltersDto.location()
                        .trim()
                        .toLowerCase()));
    }
}
