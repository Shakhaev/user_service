package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.util.List;

public record EventDto(
        Long id,
        String title,
        String startDate,
        String endDate,
        Long ownerId,
        String description,
        List<Long> relatedSkillsIds,
        String location,
        int maxAttendees,
        EventType eventType,
        EventStatus eventStatus) {}
