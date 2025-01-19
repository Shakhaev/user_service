package school.faang.user_service.dto;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Builder
public record MentorshipRequestDto(
        Long id,
        String description,
        Long requesterUserId,
        Long receiverUserId,
        RequestStatus status,
        String rejectionReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}