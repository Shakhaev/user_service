package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.RecommendationService;

import java.util.List;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        validationRecommendationIsContentEmpty(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        validationRecommendationIsContentEmpty(updated);
        return recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

//    public Stream<User> getPremiumUsers(UserFilterDto filter) {
//        return (Stream<User>) recommendationService.getPremiumUsers(filter);
//    }

    private void validationRecommendationIsContentEmpty(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().isEmpty()) {
            throw new DataValidationException("Рекомендация должна содержать текст");
        }
    }
}
