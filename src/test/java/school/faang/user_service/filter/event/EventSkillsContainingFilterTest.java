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

@ExtendWith(MockitoExtension.class)
class EventSkillsContainingFilterTest {
    @InjectMocks
    EventSkillsContainingFilter skillsFilter;

    @Test
    void isApplicable_ShouldNotApplyWhenNoSkillsSet() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        Assertions.assertFalse(skillsFilter.isApplicable(eventFilterDto));
    }

    @Test
    void apply_ShouldCorrectlyFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setRelatedSkillsIds(List.of(1L, 2L));
        Skill s1 = new Skill();
        s1.setId(1L);
        Skill s2 = new Skill();
        s2.setId(2L);
        Skill s3 = new Skill();
        s3.setId(3L);
        Skill s4 = new Skill();
        s4.setId(4L);

        Event correctEvent1 = new Event();
        correctEvent1.setRelatedSkills(List.of(s1, s2, s3, s4));
        Event correctEvent2 = new Event();
        correctEvent2.setRelatedSkills(List.of(s1, s2));

        Event wrongEvent1 = new Event();
        wrongEvent1.setRelatedSkills(List.of(s3, s4));
        Event wrongEvent2 = new Event();
        wrongEvent2.setRelatedSkills(List.of(s1, s3, s4));
        Event wrongEvent3 = new Event();
        wrongEvent3.setRelatedSkills(List.of());

        Stream<Event> eventStream = Stream.of(correctEvent1, correctEvent2, wrongEvent1, wrongEvent2, wrongEvent3);

        eventStream = skillsFilter.apply(eventStream, eventFilterDto);

        Assertions.assertEquals(new HashSet<>(Set.of(correctEvent1, correctEvent2)),
                eventStream.collect(Collectors.toCollection(HashSet::new)));
    }
}