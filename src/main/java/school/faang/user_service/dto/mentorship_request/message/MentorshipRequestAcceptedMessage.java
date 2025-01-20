package school.faang.user_service.dto.mentorship_request.message;

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
public class MentorshipRequestAcceptedMessage {
    private Long requestId;
    private String receiverName;
    private Long actorId;
}
