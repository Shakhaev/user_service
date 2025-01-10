package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventFilterOwnerNameTest {
    private EventFiltersDto matchName;
    private EventFiltersDto notMatchName;
    private EventFiltersDto nullName;
    private Event firstEvent;
    private Event secondEvent;
    private final EventFilter eventFilter = new EventFilterOwnerName();

    @BeforeEach
    void setUp() {
        firstEvent = new Event();
        secondEvent = new Event();
        User firstUser = User.builder().username("John").build();
        User secondUser = User.builder().username("Jane").build();
        firstEvent.setOwner(firstUser);
        secondEvent.setOwner(secondUser);

        matchName = EventFiltersDto.builder().ownerName("   JoHn ").build();
        notMatchName = EventFiltersDto.builder().ownerName("Sergio").build();
        nullName = EventFiltersDto.builder().ownerName(null).build();
    }

    @Test
    void testIsApplicableReturnsTrueOwnerNameNotNull() {
        boolean result = eventFilter.isApplicable(notMatchName);

        assertTrue(result);
    }

    @Test
    void testIsApplicableReturnsFalseOwnerNameIsNull() {
        boolean result = eventFilter.isApplicable(nullName);

        assertFalse(result);
    }

    @Test
    void testApplyFiltersEventsOwnerNameMatches() {
        List<Event> events = getEvents(matchName);

        assertEquals(1, events.size());
        assertEquals("John", events.get(0).getOwner().getUsername());
    }

    @Test
    void testApplyDoesNotFilterEventsOwnerNameNotMatch() {
        List<Event> events = getEvents(notMatchName);

        assertTrue(events.isEmpty());
    }

    private List<Event> getEvents(EventFiltersDto toCheck) {
        return eventFilter
                .apply(Stream.of(firstEvent, secondEvent), toCheck)
                .toList();
    }
}