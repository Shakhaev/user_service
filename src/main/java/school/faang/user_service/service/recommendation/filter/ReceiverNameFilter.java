package school.faang.user_service.service.recommendation.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.recommendation.Filter;

import java.util.stream.Stream;

@Component
public class ReceiverNameFilter implements Filter<RequestFilterDto, RecommendationRequest> {
    @Override
    public boolean isApplicable(@NotNull RequestFilterDto filterDto) {
        return filterDto.getReceiverName() != null && !filterDto.getReceiverName().isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> apply(@NotNull Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(request -> request.getReceiver().getUsername().matches(filterDto.getReceiverName()));
    }
}
