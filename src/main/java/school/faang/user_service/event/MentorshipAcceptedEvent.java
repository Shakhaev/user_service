package school.faang.user_service.event;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MentorshipAcceptedEvent {
    private final Long mentorshipRequestId;
    private final Long receiverId;
    private final Long requesterId;
}
