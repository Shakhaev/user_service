package school.faang.user_service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserProfileDeactivatedEvent extends ApplicationEvent {
    private Long userId;

    public UserProfileDeactivatedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
