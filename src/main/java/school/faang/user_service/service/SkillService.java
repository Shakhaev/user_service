package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;

    public SkillDto create(SkillDto skillDto) {
        Skill skill = skillMapper.dtoToEntity(skillDto);

        skillValidator.validateExistTitle(skillDto.getTitle());

        skillRepository.save(skill);
        return skillMapper.entityToDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills.stream()
                .map(skill -> skillMapper.entityToDto(skill))
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        return skills.stream()
                .collect(Collectors.groupingBy(skill -> skillMapper.entityToDto(skill), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillValidator.validateUserSkillExist(skillId, userId);
        skillValidator.validateSkillOfferCount(skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);
        Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);
        return skill.map(skill1 -> skillMapper.entityToDto(skill1))
                .orElseThrow(() -> new DataValidationException("Скилл не найден"));
    }
}
