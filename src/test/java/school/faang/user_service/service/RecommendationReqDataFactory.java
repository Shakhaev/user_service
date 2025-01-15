package school.faang.user_service.service;

import school.faang.user_service.dto.recommendation.RecommendationRequestResponseDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.List;

public final class RecommendationReqDataFactory {

    private RecommendationReqDataFactory() {
    }

    public static User createRequester() {
        return User.builder()
                .id(1L)
                .username("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    public static User createReceiver() {
        return User.builder()
                .id(2L)
                .username("Jane Doe")
                .email("jane.doe@example.com")
                .build();
    }

    public static RecommendationRequest createRecommendationRequest() {
        return RecommendationRequest.builder()
                .id(1L)
                .requester(createRequester())
                .receiver(createReceiver())
                .message("Please provide a recommendation.")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static RecommendationRequestCreateDto createRecommendationRequestSaveDto() {
        return new RecommendationRequestCreateDto(
                "Please provide a recommendation.",
                List.of(1L, 2L),
                1L,
                2L
        );
    }

    public static RecommendationRequestResponseDto createRecommendationRequestDto() {
        return new RecommendationRequestResponseDto(
                1L,
                "Please provide a recommendation.",
                RequestStatus.PENDING,
                List.of("Skill1", "Skill2"),
                1L,
                2L
        );
    }

    public static RejectionDto createRejectionDto() {
        return new RejectionDto("Not suitable");
    }
}
