package school.faang.user_service.dto.mentorship_request.message.builder;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.message.MentorshipRequestAcceptedMessage;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class MentorshipRequestAcceptedMessageBuilder {
    public MentorshipRequestAcceptedMessage build(MentorshipRequest request) {
        Long requestId = request.getId();
        String receiverName = request.getRequester().getUsername();
        Long actorId = request.getRequester().getId();

        return MentorshipRequestAcceptedMessage.builder()
                .requestId(requestId)
                .receiverName(receiverName)
                .actorId(actorId)
                .build();
    }
}
