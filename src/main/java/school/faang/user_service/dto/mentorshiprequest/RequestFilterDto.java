package school.faang.user_service.dto.mentorshiprequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
}
