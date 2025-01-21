package school.faang.user_service.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;

@RequiredArgsConstructor
@Service
public class SkillRequestService {
    private final SkillRepository skillRepository;

    public List<SkillRequest> createSkillRequests(RecommendationRequest recommendationRequest, List<Long> skillIds) {
        return skillIds.stream()
            .map(skillId -> SkillRequest.builder()
                .request(recommendationRequest)
                .skill(skillRepository.findById(skillId).orElseThrow())
                .build()
            )
            .toList();
    }
}
