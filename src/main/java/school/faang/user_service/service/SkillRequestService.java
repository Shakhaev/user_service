package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillRequestService {

    private final SkillService skillService;
    private final SkillRequestRepository skillRequestRepository;

    public List<SkillRequest> createSkillRequests(Long recommendationRequestId,
                                                  List<Long> skillIds) {
        return skillIds.stream()
                .map(skillService::findById)
                .map(skill -> skillRequestRepository.create(recommendationRequestId, skill.getId()))
                .toList();
    }
}
