package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.SkillRequestService;
import school.faang.user_service.service.recommendation.SkillService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillRequestServiceImpl implements SkillRequestService {

    private final SkillService skillService;
    private final SkillRequestRepository skillRequestRepository;

    @Override
    public List<SkillRequest> createSkillRequests(Long recommendationRequestId,
                                                  List<Long> skillIds) {
        List<Skill> skills = skillIds.stream()
                .map(skillService::findById)
                .toList();
        return skills.stream()
                .map(skill -> skillRequestRepository.create(recommendationRequestId, skill.getId()))
                .toList();
    }
}
