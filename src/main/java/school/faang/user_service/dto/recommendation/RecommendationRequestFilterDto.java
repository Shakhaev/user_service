package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
public class RecommendationRequestFilterDto {
    private String requesterPattern;
    private String receiverPattern;
    private String skillPattern;
    private RequestStatus status;
}
