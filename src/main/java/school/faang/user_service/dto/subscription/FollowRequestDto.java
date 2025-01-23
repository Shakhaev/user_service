package school.faang.user_service.dto.subscription;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowRequestDto {
    private long followerId;
    private long followeeId;
}
