package school.faang.user_service.filter.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
public class EventFilterOwnerName implements EventFilter {
    @Override
    public boolean isApplicable(EventFiltersDto eventFiltersDto) {
        return eventFiltersDto.ownerName() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFiltersDto eventFiltersDto) {
        log.info("Filtering Event by owner name: {}", eventFiltersDto.ownerName());
        return events.filter(event -> Optional.ofNullable(event.getOwner())
                .map(User::getUsername)
                .orElse("")
                .toLowerCase()
                .contains(eventFiltersDto.ownerName()
                        .trim()
                        .toLowerCase()));
    }
}
