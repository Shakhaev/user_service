package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.event.filter.TestData.ALL_EVENTS;

class EventEndDateFilterTest {
    private final EventEndDateFilter eventEndDateFilter = new EventEndDateFilter();
    private EventFilterDto filter;
    private List<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS;

        filter = new EventFilterDto();
        filter.setEndDatePattern(LocalDateTime.of(2024, 6, 1, 12, 0));

        expectedFilteredEvents = Stream.of(ALL_EVENTS.get(1));
    }

    @Nested
    class positiveTests {
        @DisplayName("should return true when pattern isn't empty")
        @Test
        void shouldReturnTrueWhenPatternIsntEmpty() {
            var isApplicable = eventEndDateFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered events")
        @Test
        void shouldReturnFilteredEvents() {
            var actualFilteredUsers = eventEndDateFilter.apply(eventsToFilter, filter);

            assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
        }

    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when empty pattern is passed")
        @Test
        void shouldReturnFalseWhenPatternIsEmpty() {
            filter.setEndDatePattern(null);

            var isApplicable = eventEndDateFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }
    }


    @DisplayName("should return empty list when no one event matched passed filter")
    @Test
    void shouldReturnEmptyListWhenNothingMatchedFilter() {
        filter.setEndDatePattern(LocalDateTime.of(2024, 3, 10, 12, 0));

        var actualFilteredUsers = eventEndDateFilter.apply(eventsToFilter, filter);

        assertEquals(List.of(), actualFilteredUsers.toList());
    }
}