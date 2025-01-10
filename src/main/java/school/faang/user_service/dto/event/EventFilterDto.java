package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private String location;
    private Long ownerId;
    private List<Long> relatedSkillsIds;
    private EventType type;
}
