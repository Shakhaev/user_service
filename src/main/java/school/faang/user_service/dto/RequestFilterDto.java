package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}
