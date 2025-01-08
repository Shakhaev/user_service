package school.faang.user_service.dto.response;

import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class RecommendationRequestResponseDto {

    private String requester;
    private String receiver;
    private String message;
    private String status;
    private String rejectionReason;
//    private Recommendation recommendation;
    private List<String> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
