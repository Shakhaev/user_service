package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestEvent {
    private long receiverId;
    private long actorId;
    private LocalDateTime time;
}
