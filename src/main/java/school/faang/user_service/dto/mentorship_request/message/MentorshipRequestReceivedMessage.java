package school.faang.user_service.dto.mentorship_request.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestReceivedMessage {
    private Long requestId;
    private Long receiverId;
    private Long actorId;
    private LocalDateTime receivedAt;
}
