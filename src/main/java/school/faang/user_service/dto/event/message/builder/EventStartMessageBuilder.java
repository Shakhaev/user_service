package school.faang.user_service.dto.event.message.builder;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.message.EventStartMessage;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Component
public class EventStartMessageBuilder {
    public EventStartMessage build(Event event) {
        Long id = event.getId();
        String title = event.getTitle();
        List<Long> attendeeIds = event.getAttendees().stream()
                .map(User::getId)
                .toList();

        return EventStartMessage.builder()
                .id(id)
                .title(title)
                .attendeeIds(attendeeIds)
                .build();
    }
}
