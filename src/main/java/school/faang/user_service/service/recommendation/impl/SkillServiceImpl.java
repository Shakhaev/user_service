package school.faang.user_service.service.recommendation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.recommendation.SkillService;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public Skill findById(long id) {
        return skillRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::skillNotFoundException);
    }
}
