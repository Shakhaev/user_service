package school.faang.user_service.service.filter;

import lombok.Builder;

@Builder
public record RequestFilterDto(
        String descriptionPattern,
        String authorPattern,
        String receiverPattern,
        String statusPattern
) {
}
