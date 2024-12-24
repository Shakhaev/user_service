package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStartEvent {
    private long eventId;
    private List<Long> attendeesIds;
}
