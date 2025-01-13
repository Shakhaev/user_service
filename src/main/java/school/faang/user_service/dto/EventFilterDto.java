package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String titlePattern;
    private LocalDateTime startDatePattern;
    private LocalDateTime endDatePattern;
    private String locationPattern;
    private String ownerPattern;
    private String eventTypePattern;
    private String eventStatusPattern;
}
