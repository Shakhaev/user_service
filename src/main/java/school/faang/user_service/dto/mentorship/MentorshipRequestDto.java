package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class MentorshipRequestDto {
    private long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private String rejectionReason;
    private String status;
}
