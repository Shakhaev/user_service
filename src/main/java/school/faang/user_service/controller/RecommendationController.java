package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.implement.RecommendationService;

@RestController
@RequestMapping("/api/v1/users/recommendation")
@Validated
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public RecommendationDto createRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody @Valid RecommendationDto updatedRecommend) {
        return recommendationService.update(updatedRecommend);
    }

    @DeleteMapping
    public void deleteRecommendation(@NotNull(message = "Поле recommendId отсутствует") @Min(1) long recommendId) {
        recommendationService.delete(recommendId);
    }

    @GetMapping("/all-by-receiver/{id}")
    public List<RecommendationDto> getAllUserRecommendation(@PathVariable("id") long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/all-by-author/{id}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable("id")long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
