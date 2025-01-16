package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.dto.event.constraints.EnumValidator;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.util.List;

@Builder
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

        @NotNull
        @EnumValidator(enumClass = EventType.class)
        String eventType,

        @NotNull
        @EnumValidator(enumClass = EventStatus.class)
        String eventStatus) {}
