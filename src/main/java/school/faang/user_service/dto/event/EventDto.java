package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;
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
    private final String title;
    @NotNull(message = "Event Start time is mandatory")
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    @NotNull(message = "Event Owner is mandatory")
    private final Long ownerId;
    private final String description;
    private final List<Long> relatedSkills;
    private final String location;
    private final int maxAttendees;
    private final EventType eventType;
    private final EventStatus eventStatus;
}
