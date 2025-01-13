package school.faang.user_service.filter.recommendation;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.request.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
@RequiredArgsConstructor
public class RequestIdFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getRequesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> entity, RequestFilterDto filterDto) {
        if (entity == null || filterDto == null) {
            return Stream.empty();
        }

        return entity
            .filter(
                requestIds -> requestIds.getRequester().getId().equals(filterDto.getRequesterId()));
    }
}
