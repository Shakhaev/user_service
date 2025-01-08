package school.faang.user_service.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventFilters(
        String title,
        LocalDateTime startDate,
        String location,
        String ownerName) {
}
