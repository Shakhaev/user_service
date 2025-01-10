package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.validation.CustomValidation;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@CustomValidation
public class EventDto {
    private final Long id;

    @NotBlank (message = "Event Title cannot be empty")
    @With
    private final String title;
    @NotNull(message = "Event Start time is mandatory")
    @With private final LocalDateTime startTime;
    @With private final LocalDateTime endTime;
    @NotNull(message = "Event Owner is mandatory")
    @With private final Long ownerId;
    @With private final String description;
    @With private final List<Long> relatedSkills;
    @With private final String location;
    @With private final int maxAttendees;
    @With private final EventType eventType;
    @With private final EventStatus eventStatus;
}
