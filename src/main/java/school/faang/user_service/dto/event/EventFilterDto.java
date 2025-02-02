package school.faang.user_service.dto.event;

import java.time.LocalDateTime;

public record EventFilterDto(
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location,
        Long ownerId
) {
}
