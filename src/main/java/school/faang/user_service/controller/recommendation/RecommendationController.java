package school.faang.user_service.controller.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.recommendation.ControllerRecommendationValidator;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ControllerRecommendationValidator recommendationValidator;

    @PutMapping("/update")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updateRecommendationDto) {
        log.info("A request has been received to update the recommendation {}", updateRecommendationDto);
        recommendationValidator.validateContentRecommendation(updateRecommendationDto.getContent());
        return recommendationService.updateRecommendation(updateRecommendationDto);
    }

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        log.info("A request has been received for a recommendation {}", recommendationDto);
        recommendationValidator.validateContentRecommendation(recommendationDto.getContent());
        return recommendationService.giveRecommendation(recommendationDto);
    }

    @DeleteMapping("/delete")
    public void deleteRecommendation(@RequestBody RecommendationDto delRecommendationDto) {
        log.info("A request was received to delete the recommendation {}", delRecommendationDto);
        recommendationService.deleteRecommendation(delRecommendationDto);
    }

    @GetMapping("/{receiverId}/all")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        log.info("Request to receive all user {} recommendations", receiverId);
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/{authorId}/allgiven")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId){
        log.info("Request to receive all recommendations created by the user {}", authorId);
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
