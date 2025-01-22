package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class EventStartDateFilterTest {
    @InjectMocks
    EventStartDateFilter dateFilter;

    @Test
    void isApplicable_ShouldNotApplyWhenNoSkillsSet() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        Assertions.assertFalse(dateFilter.isApplicable(eventFilterDto));
    }

    @Test
    void apply_ShouldCorrectlyFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        LocalDateTime filterDate = LocalDateTime.of(2000, 11, 30, 12, 30, 30);
        eventFilterDto.setStartDate(filterDate);

        Event correctEvent1 = new Event();
        correctEvent1.setStartDate(filterDate.plusHours(5));
        Event correctEvent2 = new Event();
        correctEvent2.setStartDate(filterDate.minusMinutes(200));

        Event wrongEvent1 = new Event();
        wrongEvent1.setStartDate(filterDate.plusMonths(1));
        Event wrongEvent2 = new Event();
        wrongEvent2.setStartDate(filterDate.plusYears(1));
        Event wrongEvent3 = new Event();
        wrongEvent3.setStartDate(filterDate.plusHours(24));

        Stream<Event> eventStream = Stream.of(correctEvent1, correctEvent2, wrongEvent1, wrongEvent2, wrongEvent3);

        eventStream = dateFilter.apply(eventStream, eventFilterDto);

        Assertions.assertEquals(new HashSet<>(Set.of(correctEvent1, correctEvent2)),
                eventStream.collect(Collectors.toCollection(HashSet::new)));
    }
}