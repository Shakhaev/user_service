package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MentorshipRequestedEvent {

    private long requesterId;
    private long receiverId;
    private LocalDateTime requestedAt;
}
