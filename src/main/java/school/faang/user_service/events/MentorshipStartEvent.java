package school.faang.user_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorshipStartEvent {
    private long mentorId;
    private long menteeId;
    private long userContextId;
}
