package school.faang.user_service.filter.mentorshiprequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Component
public class MentorshipRequestIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.requestId() != null;
    }

    @Override
    public List<MentorshipRequest> apply(List<MentorshipRequest> requests, RequestFilterDto filterDto) {
        return requests.stream()
                .filter(recommendationRequest -> recommendationRequest.getId() == filterDto.requestId())
                .toList();
    }
}
