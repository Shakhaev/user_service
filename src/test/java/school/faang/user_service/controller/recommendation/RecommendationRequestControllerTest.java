package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.BaseTest;
import school.faang.user_service.data.RecommendationRequestData;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

public class RecommendationRequestControllerTest extends BaseTest {
    @Autowired
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void requestRecommendationSuccess() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        RecommendationRequestDto response = recommendationRequestController.requestRecommendation(createDataAndGetDto(data));
        Assertions.assertNotNull(response);

        RecommendationRequest request = recommendationRequestRepository.findById(response.getId()).get();

        Assertions.assertNotNull(request);
        Assertions.assertEquals(data.getMessage(), request.getMessage());
        Assertions.assertEquals(RequestStatus.PENDING.name(), request.getStatus().name());
    }

    @Test
    public void requestRecommendationLessMinMonthFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;
        RecommendationRequestDto dto = createDataAndGetDto(data);
        recommendationRequestController.requestRecommendation(dto);

        try {
            recommendationRequestController.requestRecommendation(dto);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Less than min months have passed since the previous request", e.getMessage());
        }
    }

    @Test
    public void requestRecommendationNotFoundUserFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        try {
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Requester or receiver not found", e.getMessage());
        }
    }

    @Test
    public void requestRecommendationNullMessageFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA_NULL_MESSAGE;

        try {
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("message must not be null", e.getMessage());
        }
    }

    @Test
    void getRecommendationRequests() {
    }

    @Test
    void getRecommendationRequest() {
    }

    private RecommendationRequestDto createDataAndGetDto(RecommendationRequestData data) {
        Country country = createCountry(data.getRequester().getCountry());

        User requester = createUser(data.getRequester().getUser(country));
        User receiver;
        if (!data.getRequester().equals(data.getReceiver())) {
            receiver = createUser(data.getReceiver().getUser(country));
        } else {
            receiver = requester;
        }
        return data.toDto(
                requester.getId(),
                receiver.getId(),
                data.getSkillsRequested().stream().map(skillData -> createSkill(skillData).getId()).toList());
    }
}