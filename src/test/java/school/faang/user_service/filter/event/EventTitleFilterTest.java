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

class EventTitleFilterTest {
    private EventFilters matchTitle;
    private EventFilters notMatchTitle;
    private EventFilters nullTitle;
    private Event firstEvent;
    private Event secondEvent;
    private final EventFilter eventFilter = new EventTitleFilter();

    @BeforeEach
    void setUp() {
        firstEvent = new Event();
        secondEvent = new Event();

        firstEvent.setTitle("Event in Moscow city");
        secondEvent.setTitle("Cupertino event, USA");

        matchTitle = EventFilters.builder().title("  MOScoW  ").build();
        notMatchTitle = EventFilters.builder().title("spb").build();
        nullTitle = EventFilters.builder().title(null).build();
    }

    @Test
    void testIsApplicable_ReturnsTrue_WhenTitleIsNotNull() {
        boolean result = eventFilter.isApplicable(notMatchTitle);

        assertTrue(result);
    }

    @Test
    void testIsApplicable_ReturnsFalse_WhenTitleIsNull() {
        boolean result = eventFilter.isApplicable(nullTitle);

        assertFalse(result);
    }

    @Test
    void testApply_FiltersEvents_WhenTitleMatches() {
        List<Event> events = getEvents(matchTitle);

        assertEquals(1, events.size());
        assertEquals(firstEvent.getTitle(), events.get(0).getTitle());
    }

    @Test
    void testApply_DoesNotFilterEvents_WhenTitleNotMatch() {
        List<Event> events = getEvents(notMatchTitle);

        assertTrue(events.isEmpty());
    }

    private List<Event> getEvents(EventFilters toCheck) {
        return eventFilter
                .apply(Stream.of(firstEvent, secondEvent), toCheck)
                .toList();
    }
}