package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EventRequestDto(
        @NotBlank
        String title,

        @NotNull
        String startDate,

        String endDate,

        @NotNull
        Long ownerId,

        @NotBlank
        String description,

        List<Long> relatedSkillsIds,
        String location,
        int maxAttendees,
        String eventType,
        String eventStatus) {}
