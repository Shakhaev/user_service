package school.faang.user_service.dto.event;

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
        String eventType,
        String eventStatus) {}
