package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;

    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);
        Skill skillEntity = skillMapper.toEntitySkill(skillDto);
        skillEntity = skillRepository.save(skillEntity);
        return skillMapper.toDtoSkill(skillEntity);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Навык должен иметь название");
        }
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Такой навык уже существует");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> allSkillEntityByUserId = skillRepository.findAllByUserId(userId);
        return skillMapper.toDtoSkill(allSkillEntityByUserId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        Map<SkillDto, Long> skillCounts = skillsOfferedToUser.stream()
                .map(skill -> skillMapper.toDtoSkill(skill))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<SkillCandidateDto> skillCandidateDtos = new ArrayList<>();

        for (Map.Entry<SkillDto, Long> entry : skillCounts.entrySet()) {
            SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
            skillCandidateDto.setSkill(entry.getKey());
            skillCandidateDto.setOffersAmount(entry.getValue());
            skillCandidateDtos.add(skillCandidateDto);
        }
        return skillCandidateDtos;
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(() -> new NoSuchElementException("Скилл не найден"));

        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        if (!userSkill.isPresent()) {
            List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (allOffersOfSkill.size() >= 3) {
                skillRepository.assignSkillToUser(skillId, userId);
            }
        }
        return skillMapper.toDtoSkill(skill);
    }
}
