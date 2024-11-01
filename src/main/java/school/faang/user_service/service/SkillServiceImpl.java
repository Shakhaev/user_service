package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.web.dto.mapper.SkillMapper;
import school.faang.user_service.web.dto.skill.SkillCandidateDto;
import school.faang.user_service.web.dto.skill.SkillDto;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with this title already exists");
        }

        return Optional.of(skillDto)
                .map(skillMapper::toEntity)
                .map(skillRepository::saveAndFlush)
                .map(skillMapper::toDto)
                .orElseThrow(() -> new DataValidationException("Error save product"));
    }

    @Override
    public Optional<List<SkillDto>> getUserSkills(Long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return Optional.ofNullable(skills)
                .map(list -> list.stream()
                        .map(skillMapper::toDto)
                        .toList());
    }

    @Override
    public Optional<List<SkillCandidateDto>> getOfferedSkills(Long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        return Optional.of(skills.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillMapper.toCandidateDto(entry.getKey(), entry.getValue()))
                .toList());
    }

    @Override
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        if (skillRepository.findUserSkill(userId, skillId).isPresent()) {
            throw new DataValidationException("User already possesses this skill.");
        }

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough offers to acquire this skill.");
        }

        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(offers);

        return skillMapper.toDto(offers.get(0).getSkill());
    }

    private void addUserSkillGuarantee(List<SkillOffer> offeredSkills) {
        userSkillGuaranteeRepository.saveAll(offeredSkills.stream()
                .map(offeredSkill -> UserSkillGuarantee.builder()
                        .user(offeredSkill.getRecommendation().getReceiver())
                        .skill(offeredSkill.getSkill())
                        .guarantor(offeredSkill.getRecommendation().getAuthor())
                        .build()
                )
                .distinct()
                .toList());
    }
}

