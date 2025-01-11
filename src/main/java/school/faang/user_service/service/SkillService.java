package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.controller.SkillController;
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
    private final SkillController skillController;

    public SkillDto create(SkillDto skill) throws DataValidationException {
        SkillDto skillDto = skillController.create(skill);

        Skill skillEntity = skillMapper.toEntity(skillDto);

        if (!skillRepository.existsByTitle(skillEntity.getTitle())) {
            skillEntity = skillRepository.save(skillEntity);
            return skillMapper.toDto(skillEntity);
        } else {
            throw new DataValidationException("The skill already exists!");
        }
    }
}
