package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;
import school.faang.user_service.validator.RequestValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation-requests")
public class RecommendationRequestController {
    private final RequestValidator requestValidator;
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequestDto) {
        requestValidator.validateRecomendationRequest(recommendationRequestDto);
        return recommendationRequestService.create(recommendationRequestDto);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
