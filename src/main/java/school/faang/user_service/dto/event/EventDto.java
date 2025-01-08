package school.faang.user_service.dto.event;

import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location,
        int maxAttendees,
        Long ownerId,
        List<Long> relatedSkillIds,
        EventType type,
        EventStatus status) {
}