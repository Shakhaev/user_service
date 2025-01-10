package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventLocationFilterTest {
    private EventFiltersDto matchLocation;
    private EventFiltersDto notMatchLocation;
    private EventFiltersDto nullLocation;
    private Event firstEvent;
    private Event secondEvent;
    private final EventFilter eventFilter = new EventLocationFilter();

    @BeforeEach
    void setUp() {
        firstEvent = new Event();
        secondEvent = new Event();

        firstEvent.setLocation("Moscow");
        secondEvent.setLocation("Cupertino");

        matchLocation = EventFiltersDto.builder().location("  MOScoW  ").build();
        notMatchLocation = EventFiltersDto.builder().location("spb").build();
        nullLocation = EventFiltersDto.builder().location(null).build();
    }

    @Test
    void testIsApplicableReturnsTrueLocationNotNull() {
        boolean result = eventFilter.isApplicable(notMatchLocation);

        assertTrue(result);
    }

    @Test
    void testIsApplicableReturnsFalseLocationIsNull() {
        boolean result = eventFilter.isApplicable(nullLocation);

        assertFalse(result);
    }

    @Test
    void testApplyFiltersEventsLocationMatches() {
        List<Event> events = getEvents(matchLocation);

        assertEquals(1, events.size());
        assertEquals("Moscow", events.get(0).getLocation());
    }

    @Test
    void testApplyDoesNotFilterEventsLocationNotMatch() {
        List<Event> events = getEvents(notMatchLocation);

        assertTrue(events.isEmpty());
    }

    private List<Event> getEvents(EventFiltersDto toCheck) {
        return eventFilter
                .apply(Stream.of(firstEvent, secondEvent), toCheck)
                .toList();
    }

}