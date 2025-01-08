package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventLocationFilterTest {
    private EventFilters matchLocation;
    private EventFilters notMatchLocation;
    private EventFilters nullLocation;
    private Event firstEvent;
    private Event secondEvent;
    private final EventFilter eventFilter = new EventLocationFilter();

    @BeforeEach
    void setUp() {
        firstEvent = new Event();
        secondEvent = new Event();

        firstEvent.setLocation("Moscow");
        secondEvent.setLocation("Cupertino");

        matchLocation = EventFilters.builder().location("  MOScoW  ").build();
        notMatchLocation = EventFilters.builder().location("spb").build();
        nullLocation = EventFilters.builder().location(null).build();
    }

    @Test
    void testIsApplicable_ReturnsTrue_WhenLocationIsNotNull() {
        boolean result = eventFilter.isApplicable(notMatchLocation);

        assertTrue(result);
    }

    @Test
    void testIsApplicable_ReturnsFalse_WhenLocationIsNull() {
        boolean result = eventFilter.isApplicable(nullLocation);

        assertFalse(result);
    }

    @Test
    void testApply_FiltersEvents_WhenLocationMatches() {
        List<Event> events = getEvents(matchLocation);

        assertEquals(1, events.size());
        assertEquals("Moscow", events.get(0).getLocation());
    }

    @Test
    void testApply_DoesNotFilterEvents_WhenLocationNotMatch() {
        List<Event> events = getEvents(notMatchLocation);

        assertTrue(events.isEmpty());
    }

    private List<Event> getEvents(EventFilters toCheck) {
        return eventFilter
                .apply(Stream.of(firstEvent, secondEvent), toCheck)
                .toList();
    }

}