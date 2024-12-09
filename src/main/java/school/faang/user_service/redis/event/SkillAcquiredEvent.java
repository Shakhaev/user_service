package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillAcquiredEvent {
    private long recommenderId;
    private long recipientId;
    private long skillId;
}
