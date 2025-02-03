package school.faang.user_service.filter.mentorshiprequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.receiverId() != null;
    }

    @Override
    public List<MentorshipRequest> apply(List<MentorshipRequest> requests, RequestFilterDto filterDto) {
        return requests.stream()
                .filter(recommendationRequest ->
                recommendationRequest.getReceiver().getId().equals(filterDto.receiverId()))
                .toList();
    }
}
