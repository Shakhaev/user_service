package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record MentorshipRequestFilterDto(
        String descriptionPattern,
        String authorPattern,
        String receiverPattern,
        String statusPattern
) {
}
