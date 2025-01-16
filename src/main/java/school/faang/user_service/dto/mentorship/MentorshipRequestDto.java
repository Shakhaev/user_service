package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class MentorshipRequestDto {
    private long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private String rejectionReason;
    private RequestStatus status;
}
