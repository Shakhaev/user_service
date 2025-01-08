package school.faang.user_service.dto.mentorshiprequest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MentorshipAcceptedEvent {
    private long id;
    private long authorId;
    private long receiverId;
    private LocalDateTime acceptedAt;
}
