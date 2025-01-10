package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SkillRequestService {
    private final SkillRequestRepository skillRequestRepository;
    private final SkillMapper skillMapper;

    public void createAllSkillRequest(List<SkillDto> skills, RecommendationRequest recommendationRequest) {
        skillRequestRepository.saveAll(skills.stream().map(skillDto -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(recommendationRequest);
            skillRequest.setSkill(skillMapper.toEntity(skillDto));
            return skillRequest;
        }).toList());
    }
}
