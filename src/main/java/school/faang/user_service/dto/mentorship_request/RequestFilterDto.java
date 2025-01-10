package school.faang.user_service.dto.mentorship_request;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    private final String descriptionPattern;
    private final Long requesterId;
    private final Long receiverId;
    private final RequestStatus status;
}
