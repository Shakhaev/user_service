package school.faang.user_service.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validate(RecommendationRequestDto requestDto) {
        checkRequestTime(requestDto.getRequesterId(), requestDto.getReceiverId());
    }

    private void checkRequestTime(Long requesterId, Long receiverId) {
        Optional<RecommendationRequest> recommendationRequestOptional =
                recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);

        if (recommendationRequestOptional.isEmpty() ||
                    recommendationRequestOptional.get().getCreatedAt().isBefore(LocalDateTime.now().minusMonths(6))) {
            throw new NoSuchElementException(
                    "Для указанного запрашивающего и получающего не " +
                            "найдено ожидающих рассмотрения запросов на рекомендации");
        }
    }
}
