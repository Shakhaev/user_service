package school.faang.user_service.dto;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record MentorshipRequestDto(
        String description,
        UserDto requester,
        UserDto receiver,
        RequestStatus status,
        String rejectionReason
) {
}