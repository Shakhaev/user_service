package school.faang.user_service.dto.recommendation;

import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record RequestFilterDto(Long requesterId,
                               Long receiverId,
                               String message,
                               RequestStatus status,
                               String rejectionReason,
                               Long recommendationId,
                               LocalDateTime createdAtFrom,
                               LocalDateTime createdAtTo,
                               LocalDateTime updatedAtFrom,
                               LocalDateTime updatedAtTo) {
}
