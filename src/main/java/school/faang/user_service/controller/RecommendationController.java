package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping(value = "/recommendations")
    public Recommendation giveRecommendation(
            @RequestBody RecommendationDto recommendation) {
        return recommendationService.giveRecommendation(recommendation);
    }
}
