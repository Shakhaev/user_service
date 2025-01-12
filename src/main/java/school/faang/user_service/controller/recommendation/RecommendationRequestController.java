package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestServiceImpl;

import java.util.List;

@Controller
@AllArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestServiceImpl recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestRcvDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("requestDto cannot be null");
        }
        if (requestDto.getMessage() == null || requestDto.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        return recommendationRequestService.create(requestDto);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        if (rejectionDto == null) {
            throw new IllegalArgumentException("rejectionDto cannot be null");
        }
        if (rejectionDto.getReason() == null || rejectionDto.getReason().isBlank()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }
        return recommendationRequestService.rejectRequest(id, rejectionDto);
    }
}
