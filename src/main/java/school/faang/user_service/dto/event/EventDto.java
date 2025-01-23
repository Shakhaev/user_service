package school.faang.user_service.dto.event;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;
    @NotNull
    private Long ownerId;
    @NotNull
    private String description;
    @NotNull
    private List<Long> relatedSkills;
    @NotNull
    private String location;
    private int maxAttendees;
    @NotNull
    @Enumerated
    private EventType eventType;
    @NotNull
    @Enumerated
    private EventStatus eventStatus;
}
