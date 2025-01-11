package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public void giveRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        recommendationService.create(recommendationDto);
    }

    public void updateRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        recommendationService.update(recommendationDto);
    }

    public void deleteRecommendation(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public void getAllUserRecommendations(long receiverId) {
        recommendationService.getAllUserRecommendations(receiverId);
    }

    public void getAllGivenRecommendations(long authorId) {
        recommendationService.getAllGivenRecommendations(authorId);
    }
}
