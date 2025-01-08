package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCreateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;

    public SkillDto create(SkillCreateDto skillCreateDto) {
        if (skillRepository.existsByTitle(skillCreateDto.getTitle())) {
            throw new DataValidationException(" Skill с таким названием уже существует .");
        }
        Skill skill = skillMapper.toEntity(skillCreateDto);
        skill = skillRepository.save(skill);
        return skillMapper.toSkillDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        validateSkillList(skills);
        return skills.stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    private void validateSkillList(List<?> skills) {
        if (skills.isEmpty()) {
            throw new NoSuchElementException("Умения не найдены .");
        }
    }
}
