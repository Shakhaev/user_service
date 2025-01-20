package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.service.RecommendationValidation;

import java.util.List;

@Slf4j
@Component
@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/give_recommendation")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        if (RecommendationValidation.textAvailability(recommendationDto)) {
            recommendationService.create(recommendationDto);
            return recommendationDto;
        }
        throw new DataValidationException("В рецензии должен содержатся текст");
    }

    @PutMapping("/recommendation")
    @Transactional
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        if (RecommendationValidation.textAvailability(updated)) {
            recommendationService.update(updated);
            return updated;
        }
        throw new DataValidationException("В рецензии должен содержатся текст");
    }

    @DeleteMapping("/recommendation/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @PostMapping("/get_all_user_recommendations/{recieverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long recieverId) {
        return recommendationService.getAllUserRecommendations(recieverId);
    }

    @PostMapping("/get_all_given_recommendations/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
