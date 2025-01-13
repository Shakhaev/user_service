package school.faang.user_service.service.recommendation;

import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

public interface SkillRequestService {

    List<SkillRequest> createSkillRequests(Long recommendationRequestId, List<Long> skillIds);
}
