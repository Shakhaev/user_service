package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final AppConfig appConfig;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new IllegalArgumentException("Скилл уже существует");
        }

        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(s -> skillMapper.toDto(s))
                .toList();
    }

    public List<SkillCandidateDto> getUserOfferedSkills(@NotNull Long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }


    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        if (userSkill.isPresent()) {
            return skillMapper.toDto(userSkill.get());
        }

        skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Скилл не существует"));

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() >= appConfig.getMinSkillOffers()) {
            skillRepository.assignSkillToUser(skillId, userId);
            Skill skill = skillOffers.stream().findAny().get().getSkill();

            skillOffers.forEach(offer -> {
                UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                        .skill(skill)
                        .user(user)
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build();

                userSkillGuaranteeRepository.save(guarantee);
            });

            return skillMapper.toDto(skill);
        }

        throw new IllegalArgumentException("Не хватает рекомендаций. Минимальное количество: " + appConfig.getMinSkillOffers());
    }
}
