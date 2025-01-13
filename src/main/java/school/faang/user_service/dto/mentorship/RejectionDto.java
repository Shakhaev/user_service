package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class RejectionDto {
    private long id;
    private String rejectionReason;
    private String status;
}
