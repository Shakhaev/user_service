package school.faang.user_service.service.recommendation.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.recommendation.Filter;

import java.util.stream.Stream;

@Component
public class StatusFilter implements Filter<RecommendationRequest> {
    @Override
    public boolean isApplicable(@NotNull RequestFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(@NotNull Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(request -> request.getStatus().equals(filterDto.getStatus()));
    }
}
