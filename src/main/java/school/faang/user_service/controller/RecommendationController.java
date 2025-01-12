package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final SubscriptionService subscriptionService;

    @PostMapping(value = "/give", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    @DeleteMapping("/{recommendationId}")
    public void deleteRecommendation(@PathVariable Long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping(value = "/by-receiver-id/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping(value = "/by-author-id/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return subscriptionService.getAllGivenRecommendations(authorId);
    }
}
