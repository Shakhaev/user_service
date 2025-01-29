package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
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
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new IllegalArgumentException("Skill already exists.");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);

        return skillMapper.toDTO(savedSkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> userSkills = skillRepository.findAllByUserId(userId);
        return userSkills.stream()
                .map(skillMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {

        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);


        Map<Skill, Long> skillOffersCount = skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        return skillOffersCount.entrySet().stream()
                .map(entry -> {
                    SkillCandidateDto dto = new SkillCandidateDto();
                    dto.setSkill(skillMapper.toDTO(entry.getKey())); // Преобразуем Skill в SkillDto
                    dto.setOffersAmount(entry.getValue()); // Записываем количество предложений
                    return dto;
                })
                .toList();
    }
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {

        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        if (existingSkill.isPresent()) {
            throw new IllegalArgumentException("User already has this skill.");
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < 3) {
            throw new IllegalArgumentException("Not enough offers to acquire this skill.");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        offers.stream()
                .map(offer -> offer.getRecommendation().getAuthor().getId())
                .distinct()
                .forEach(guarantorId -> skillRepository.assignGuarantorToUser(skillId, userId, guarantorId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found."));
        return skillMapper.toDTO(skill);
    }

}
