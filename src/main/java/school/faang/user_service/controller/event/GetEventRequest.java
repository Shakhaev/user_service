package school.faang.user_service.controller.event;

import lombok.Data;
import school.faang.user_service.dto.event.EventDto;

@Data
public class GetEventRequest {
    private EventDto filter;
    private Integer limit;
    private Integer offset;
}
