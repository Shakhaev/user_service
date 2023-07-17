package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;
    private final SkillCandidateDto skillCandidateDto;

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);

        Skill skillToSave = skillMapper.toEntity(skillDto);
        List<User> users = StreamSupport.stream
                (userRepository.findAllById(skillDto.getUserIds()).spliterator(), false).toList();
        skillToSave.setUsers(users);

        return skillMapper.toDto(skillRepository.save(skillToSave));
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Enter skill title, please");
        }

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title " + skillDto.getTitle() + " already exists");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        validateUserSkills(skills);

        return skills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    private void validateUserSkills(List<Skill> skills) {
        if (skills.isEmpty()) {
            throw new DataValidationException("User has no skills");
        }
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        validateUserSkills(skills);

        Map<String, Long> skillsAmount = skills.stream().
                collect(Collectors.groupingBy(Skill::getTitle, Collectors.counting()));

        return skills.stream()
                .map(skill -> skillMapper.toCandidateDto(skill, skillsAmount.getOrDefault(skill.getTitle(), 0L)))
                .toList();
    }
}
