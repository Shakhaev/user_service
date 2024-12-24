package school.faang.user_service.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipAcceptedEvent {
    private  Long mentorshipRequestId;
    private  String description;
    private  Long receiverId;
    private  String receiverUserName;
    private  Long requesterId;
    private  String requesterUserName;
}
