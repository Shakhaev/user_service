package school.faang.user_service.filter.recommendation;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.request.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
@RequiredArgsConstructor
public class ReceiverIdFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getReceiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> entities, RequestFilterDto filterDto) {
        if (entities == null || filterDto == null) {
            return Stream.empty();
        }

        return entities
            .filter(recommendationRequest ->
                Objects.nonNull(recommendationRequest.getReceiver().getId()) &&
                    recommendationRequest.getReceiver().getId().equals(filterDto.getReceiverId()));
    }
}
