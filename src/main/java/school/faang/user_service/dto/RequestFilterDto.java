package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class RequestFilterDto {

    @Length(min = 3, max = 4096, message = "Description pattern must be between 10 and 4096 characters.")
    private String descriptionPattern;

    @Positive(message = "RequesterId must be greater than 0.")
    private Long requesterId;

    @Positive(message = "ReceiverId must be greater than 0.")
    private Long receiverId;

    private RequestStatus status;
}