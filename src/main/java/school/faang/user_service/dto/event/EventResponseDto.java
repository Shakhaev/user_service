package school.faang.user_service.dto.event;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDto {
  private Long id;
  private String title;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Long ownerId;
  private String description;
  private List<Long> relatedSkills;
  private String location;
  private Integer maxAttendees;
  private EventType eventType;
  private EventStatus eventStatus;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
