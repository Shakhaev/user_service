package school.faang.user_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {

    public static final int MIN_SKILL_OFFERS = 3;
    private SkillRepository skillRepository;
    private SkillOfferRepository skillOfferRepository;
    private UserSkillGuaranteeRepository skillGuaranteeRepository;
    private SkillMapper skillMapper;

    @Override
    public SkillDto create(SkillDto skill) {
        String title = skill.getTitle();
        if (skillRepository.existsByTitle(title)) {
            throw new DataValidationException(String.format("Skill с таким названием [%s] уже существует", title));
        }

        Skill entity = skillMapper.toSkillEntity(skill);
        Skill response = skillRepository.save(entity);
        return skillMapper.toSkillDto(response);
    }

    @Override
    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toSkillListDto(skills);
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> offersCount = new HashMap<>();

        for (Skill skill : skills) {
            offersCount.put(skill, offersCount.getOrDefault(skill, 0L) + 1);
        }

        List<SkillCandidateDto> skillDtos = new ArrayList<>();

        for (Map.Entry<Skill, Long> entry : offersCount.entrySet()) {
            SkillCandidateDto dto = SkillCandidateDto.builder()
                    .skill(skillMapper.toSkillDto(entry.getKey()))
                    .offersAmount(entry.getValue())
                    .build();
            skillDtos.add(dto);
        }

        return skillDtos;
    }

    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        if (userSkill.isPresent()) {
            return skillMapper.toSkillDto(userSkill.get());
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Недостаточно предложений для приобретения навыка");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        skillOffers.forEach(skillOffer -> {
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setSkill(skillOffer.getSkill());
            skillGuaranteeRepository.save(guarantee);
        });

        Skill assignedSkill = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new DataValidationException("Не удалось назначить навык"));
        return skillMapper.toSkillDto(assignedSkill);
    }

}
