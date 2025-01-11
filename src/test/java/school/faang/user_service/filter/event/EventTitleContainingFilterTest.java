package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventTitleContainingFilterTest {
    @InjectMocks
    EventTitleContainingFilter titleFilter;

    @Test
    void shouldNotApplyWhenNoSkillsSet() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        Assertions.assertFalse(titleFilter.isApplicable(eventFilterDto));
    }

    @Test
    void shouldCorrectlyFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setTitle("Title");

        Event correctEvent1 = new Event();
        correctEvent1.setTitle("     tITLE      ");
        Event correctEvent2 = new Event();
        correctEvent2.setTitle(" abcTitlebca ");

        Event wrongEvent1 = new Event();
        wrongEvent1.setTitle(" \n\t ");
        Event wrongEvent2 = new Event();
        wrongEvent2.setTitle(" Hello world ");

        Stream<Event> eventStream = Stream.of(correctEvent1, correctEvent2, wrongEvent1, wrongEvent2);

        eventStream = titleFilter.apply(eventStream, eventFilterDto);

        Assertions.assertEquals(new HashSet<>(Set.of(correctEvent1, correctEvent2)),
                eventStream.collect(Collectors.toCollection(HashSet::new)));
    }
}