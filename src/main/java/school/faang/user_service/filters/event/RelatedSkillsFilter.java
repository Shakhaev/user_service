package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

public class RelatedSkillsFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventDto filters) {
        return filters != null;
    }

    @Override
    public boolean filterEntity(Event event, EventDto filters) {
        return event.getRelatedSkills().parallelStream()
                .anyMatch(filters.getRelatedSkills()::contains);
    }
}
