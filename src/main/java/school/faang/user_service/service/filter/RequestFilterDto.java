package school.faang.user_service.service.filter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestFilterDto {
    private final String descriptionPattern;
    private final String authorPattern;
    private final String receiverPattern;
    private final String statusPattern;
}
