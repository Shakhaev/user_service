package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skill) throws DataValidationException {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill skillEntity = skillMapper.toEntity(skill);
            skillEntity = skillRepository.save(skillEntity);
            return skillMapper.toDto(skillEntity);
        } else {
            throw new DataValidationException("The skill already exists!");
        }
    }
}
