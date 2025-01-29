package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventFilterTests {

    @Test
    void apply_OwnerIdMatchesShouldReturnFilteredEvents() {
        OwnerIdEventFilter filter = new OwnerIdEventFilter();
        EventFilterDto dto = new EventFilterDto(null, null, null, null, 1L);

        Event event1 = createEventWithOwner(1L);
        Event event2 = createEventWithOwner(2L);

        Stream<Event> filtered = filter.apply(Stream.of(event1, event2), dto);
        List<Event> result = filtered.toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOwner().getId());
    }

    @Test
    void isApplicable_OwnerIdIsNullShouldReturnFalse() {
        OwnerIdEventFilter filter = new OwnerIdEventFilter();
        EventFilterDto dto = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void apply_StartDateMatchesShouldReturnFilteredEvents() {
        StartDateEventFilter filter = new StartDateEventFilter();
        LocalDateTime filterDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        EventFilterDto dto = new EventFilterDto(null, filterDate, null, null, null);

        Event event1 = createEventWithDate(LocalDateTime.of(2025, 1, 2, 12, 0));
        Event event2 = createEventWithDate(LocalDateTime.of(2024, 12, 31, 23, 59));

        Stream<Event> filtered = filter.apply(Stream.of(event1, event2), dto);
        List<Event> result = filtered.toList();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getStartDate().isAfter(filterDate) || result.get(0).getStartDate().isEqual(filterDate));
    }

    @Test
    void isApplicable_StartDateIsNullShouldReturnFalse() {
        StartDateEventFilter filter = new StartDateEventFilter();
        EventFilterDto dto = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    void apply_TitleContainsKeywordShouldReturnFilteredEvents() {
        TitleEventFilter filter = new TitleEventFilter();
        EventFilterDto dto = new EventFilterDto("conference", null, null, null, null);

        Event event1 = createEventWithTitle("Tech Conference 2025");
        Event event2 = createEventWithTitle("Workshop on AI");

        Stream<Event> filtered = filter.apply(Stream.of(event1, event2), dto);
        List<Event> result = filtered.toList();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().toLowerCase().contains("conference"));
    }

    @Test
    void isApplicable_TitleIsNullOrEmptyShouldReturnFalse() {
        TitleEventFilter filter = new TitleEventFilter();

        EventFilterDto emptyTitle = new EventFilterDto("", null, null, null, null);
        EventFilterDto nullTitle = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(emptyTitle));
        assertFalse(filter.isApplicable(nullTitle));
    }

    private Event createEventWithOwner(Long ownerId) {
        Event event = new Event();
        User owner = new User();
        owner.setId(ownerId);
        event.setOwner(owner);
        return event;
    }

    private Event createEventWithDate(LocalDateTime startDate) {
        Event event = new Event();
        event.setStartDate(startDate);
        return event;
    }

    private Event createEventWithTitle(String title) {
        Event event = new Event();
        event.setTitle(title);
        return event;
    }
}
