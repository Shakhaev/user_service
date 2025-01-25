package school.faang.user_service.dto.event.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStartMessage {
    private Long id;
    private String title;
    private List<Long> attendeeIds;
}
