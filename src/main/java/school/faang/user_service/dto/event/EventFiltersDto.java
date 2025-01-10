package school.faang.user_service.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventFiltersDto(
        String title,
        LocalDateTime startDate,
        String location,
        String ownerName) {}
