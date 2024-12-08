package school.faang.user_service.dto.messaging;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectFollowerEvent implements Serializable {
    private final long projectId;
    private final long followerId;
    private final long followeeId;
}
