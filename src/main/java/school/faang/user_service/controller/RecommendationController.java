package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping(value = "/give", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationDto giveRecommendation(
            @RequestBody RecommendationDto recommendation) {
        return recommendationService.giveRecommendation(recommendation);
    }

     @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Recommendation updateRecommendation(
            @RequestBody RecommendationDto updated) {
        return recommendationService.updateRecommendation(updated);
    }
}
