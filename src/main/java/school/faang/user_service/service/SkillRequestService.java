package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SkillRequestService {
    private final SkillRequestRepository skillRequestRepository;
    private final SkillService skillService;

    public List<SkillRequest> createAllSkillRequest(List<Long> skillIds, RecommendationRequest recommendationRequest) {
        List<SkillRequest> skillRequests = skillIds.stream().map(skillId -> SkillRequest.builder()
                .request(recommendationRequest)
                .skill(skillService.getSkillById(skillId))
                .build())
                .toList();
        return (List<SkillRequest>) skillRequestRepository.saveAll(skillRequests);
    }
}
