package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFilterDto {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Integer maxAttendees;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private EventType eventType;
    private EventStatus eventStatus;
    private Long ownerId;
    private List<Long> skillIds;
    private List<Long> relatedSkills;
}
