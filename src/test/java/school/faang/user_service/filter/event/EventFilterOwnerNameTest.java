package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventFilterOwnerNameTest {
    private EventFilters matchName;
    private EventFilters notMatchName;
    private EventFilters nullName;
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

        matchName = EventFilters.builder().ownerName("   JoHn ").build();
        notMatchName = EventFilters.builder().ownerName("Sergio").build();
        nullName = EventFilters.builder().ownerName(null).build();
    }

    @Test
    void testIsApplicable_ReturnsTrue_WhenOwnerNameNotNull() {
        boolean result = eventFilter.isApplicable(notMatchName);

        assertTrue(result);
    }

    @Test
    void testIsApplicable_ReturnsFalse_WhenOwnerNameIsNull() {
        boolean result = eventFilter.isApplicable(nullName);

        assertFalse(result);
    }

    @Test
    void testApply_FiltersEvents_WhenOwnerNameMatches() {
        List<Event> events = getEvents(matchName);

        assertEquals(1, events.size());
        assertEquals("John", events.get(0).getOwner().getUsername());
    }

    @Test
    void testApply_DoesNotFilterEvents_WhenOwnerNameNotMatch() {
        List<Event> events = getEvents(notMatchName);

        assertTrue(events.isEmpty());
    }

    private List<Event> getEvents(EventFilters toCheck) {
        return eventFilter
                .apply(Stream.of(firstEvent, secondEvent), toCheck)
                .toList();
    }
}