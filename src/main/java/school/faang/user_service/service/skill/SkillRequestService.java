package school.faang.user_service.service.skill;

import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

public interface SkillRequestService {
    List<SkillRequest> createAllSkillRequest(List<Long> skillIds, RecommendationRequest recommendationRequest);
}
