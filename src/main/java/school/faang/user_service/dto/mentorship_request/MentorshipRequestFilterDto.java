package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestFilterDto {
    private RequestStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Length(min = 3, max = 4096, message = "Description pattern must be between 10 and 4096 characters.")
    private String descriptionPattern;

    @Positive(message = "RequesterId must be greater than 0.")
    private Long requesterId;

    @Positive(message = "ReceiverId must be greater than 0.")
    private Long receiverId;

    private List<String> skillTitles;
}
