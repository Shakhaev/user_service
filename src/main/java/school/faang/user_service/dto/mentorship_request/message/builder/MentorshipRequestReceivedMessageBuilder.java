package school.faang.user_service.dto.mentorship_request.message.builder;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.message.MentorshipRequestReceivedMessage;
import school.faang.user_service.entity.MentorshipRequest;

import java.time.LocalDateTime;

@Component
public class MentorshipRequestReceivedMessageBuilder {
    public MentorshipRequestReceivedMessage build(MentorshipRequest request) {
        Long requestId = request.getId();
        Long receiverId = request.getReceiver().getId();
        Long actorId = request.getRequester().getId();
        LocalDateTime receivedAt = request.getCreatedAt();

        return MentorshipRequestReceivedMessage.builder()
                .requestId(requestId)
                .receiverId(receiverId)
                .actorId(actorId)
                .receivedAt(receivedAt)
                .build();
    }
}
