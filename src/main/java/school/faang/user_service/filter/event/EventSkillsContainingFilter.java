package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventSkillsContainingFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getRelatedSkillsIds() != null;
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto filters) {
        events.filter(event -> event.getRelatedSkills()
                .stream()
                .map(Skill::getId)
                .collect(Collectors.toCollection(HashSet::new))
                .containsAll(filters.getRelatedSkillsIds()));
    }
}
