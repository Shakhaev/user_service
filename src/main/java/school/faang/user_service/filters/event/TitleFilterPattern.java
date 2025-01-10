package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

import school.faang.user_service.entity.event.Event;


@Component
public class TitleFilterPattern implements EventFilter {

//    @Override
//    public boolean isApplicable(UserFilterDto filters) {
//        return filters.getAboutPattern() != null;
//    }
//
//    @Override
//    public boolean filterEntity(User user, UserFilterDto filters) {
//        return user.getAboutMe().contains(filters.getAboutPattern());
//    }

    @Override
    public boolean isApplicable(EventDto filters) {
        return false;
    }

    @Override
    public boolean filterEntity(Event event, EventDto filters) {
        return false;
    }
}
