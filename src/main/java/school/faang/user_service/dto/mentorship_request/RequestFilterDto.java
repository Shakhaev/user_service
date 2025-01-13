package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestFilterDto {
    private String descriptionPattern;
    private RequestStatus status;

    @Positive
    private Long requesterId;

    @Positive
    private Long receiverId;
}
