package school.faang.user_service.filter.mentorshiprequest;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto filterDto);

    List<MentorshipRequest> apply(List<MentorshipRequest> requests, RequestFilterDto filterDto);
}
