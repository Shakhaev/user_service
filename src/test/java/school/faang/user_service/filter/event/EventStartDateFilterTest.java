package school.faang.user_service.filter.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStartDateFilterTest {
    private EventFilters matchDate;
    private EventFilters notMatchDate;
    private EventFilters nullDate;
    private Event firstEvent;
    private Event secondEvent;
    private final EventFilter eventFilter = new EventStartDateFilter();

    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2025, 1, 8, 14, 0);

    @BeforeEach
    void setUp() {
        firstEvent = new Event();
        secondEvent = new Event();

        firstEvent.setStartDate(FIXED_NOW.plusHours(2));
        secondEvent.setStartDate(FIXED_NOW.plusHours(10));

        matchDate = EventFilters.builder()
                .startDate(FIXED_NOW.plusHours(5))
                .build();
        notMatchDate = EventFilters.builder()
                .startDate(FIXED_NOW.plusHours(12))
                .build();
        nullDate = EventFilters.builder().startDate(null).build();
    }

    @Test
    void testIsApplicable_ReturnsTrue_WhenStartDateIsNotNull() {
        boolean result = eventFilter.isApplicable(notMatchDate);

        assertTrue(result);
    }

    @Test
    void testIsApplicable_ReturnsFalse_WhenStartDateIsNull() {
        boolean result = eventFilter.isApplicable(nullDate);

        assertFalse(result);
    }

    @Test
    void testApply_FiltersEvents_WhenStartDateMatches() {
        List<Event> events = getEvents(matchDate);

        assertEquals(1, events.size());
        assertEquals(secondEvent.getStartDate(), events.get(0).getStartDate());
    }

    @Test
    void testApply_DoesNotFilterEvents_WhenStartDateNotMatch() {
        List<Event> events = getEvents(notMatchDate);

        assertTrue(events.isEmpty());
    }

    private List<Event> getEvents(EventFilters toCheck) {
        return eventFilter
                .apply(Stream.of(firstEvent, secondEvent), toCheck)
                .toList();
    }
}