package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class RejectionDto {
    private long id;
    private String rejectionReason;
    private RequestStatus status;
}
