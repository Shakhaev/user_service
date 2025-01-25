package school.faang.user_service.dto.user.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewFollowerMessage {
    private Long followeeId;
    private Long followerId;
    private String followerName;
}
