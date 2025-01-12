package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping(path = "/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationRequestDto requestRecommendation(@Valid @RequestBody RecommendationRequestSaveDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }
}
