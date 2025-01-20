package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final RecommendationValidation recommendationValidation;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillOfferRepository skillOfferRepository;

    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        recommendationValidation.checkRecommendationInterval(recommendation);
        recommendationValidation.checkingSkills(recommendation);
        Long recommendationId = recommendationRepository.create(recommendation.getId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        addSkillOffers(recommendation);
        List<Long> listGuaranteedSkills = guaranteedSkills(recommendation);
        saveMismatchedSkill(listGuaranteedSkills, recommendation);
        recommendation.setId(recommendationId);
        return recommendation;
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        recommendationValidation.checkRecommendationInterval(recommendation);
        recommendationValidation.checkingSkills(recommendation);
        recommendationRepository.update(recommendation.getAuthorId(),
                recommendation.getReceiverId(), recommendation.getContent());
        clearingSkills(recommendation);
        return recommendation;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        Page<Recommendation> entityRecommendation = recommendationRepository
                .findAllByReceiverId(recieverId, PageRequest.of(0, 1));
        return entityRecommendation.stream().map(page -> recommendationMapper.toDto(page)).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> entityRecommendation = recommendationRepository.findAllByAuthorId(authorId,
                PageRequest.of(0, 1));
        return entityRecommendation.stream().map(page -> recommendationMapper.toDto(page)).toList();
    }


    private void addSkillOffers(RecommendationDto recommendationDto) {
        List<Long> skillsId = recommendationDto.getSkillOffers().stream().map(skillOfferDto ->
                skillOfferDto.getSkillId()).toList();
        skillsId.forEach(id -> skillOfferRepository.create(id, recommendationDto.getId()));
    }

    private void saveMismatchedSkill(List<Long> skillForSave, RecommendationDto recommendationDto) {
        User receiver = userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Получатель рекомендации отсутствует"));
        User guarantor = userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Гарантер рекомендации отсутствует"));
        for (Long id : skillForSave) {//другой метод (параметры -
            Skill skill = skillRepository.findById(id)
                    .orElseThrow(() -> new DataValidationException("Скилл отсутствует"));
            UserSkillGuarantee newUserSkillGuarantee = UserSkillGuarantee.builder().user(receiver)
                    .skill(skill)
                    .guarantor(guarantor)
                    .build();
            UserSkillGuarantee userSkillGuarantee = userSkillGuaranteeRepository.save(newUserSkillGuarantee);
            skill.getGuarantees().add(userSkillGuarantee);
            receiver.getSkills().add(skill);
            skillRepository.save(skill);
            userRepository.save(receiver);
        }
    }

    private List<Long> guaranteedSkills(RecommendationDto recommendationDto) {
        Long guarantorId = recommendationDto.getAuthorId();
        User receiver = userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Получатель рекомендации отсутствует"));
        User guarantor = userRepository.findById(guarantorId)
                .orElseThrow(() -> new DataValidationException("Гарантер рекомендации отсутствует"));

        List<Skill> allSkill = skillRepository.findAllByUserId(receiver.getId());
        List<Long> existingSkill = recommendationDto.getSkillOffers().stream().map(SkillOfferDto::getSkillId).toList();
        List<Skill> matchingSkills = allSkill.stream()
                .filter(skill -> existingSkill.stream().anyMatch(existing -> existing == skill.getId()))
                .toList();
        for (Skill skill : matchingSkills) {
            List<Long> skillGuarantorId = skill.getGuarantees().stream()
                    .map(UserSkillGuarantee -> UserSkillGuarantee.getGuarantor().getId()).toList();
            boolean isNotMatchGuarantor = skillGuarantorId.stream().noneMatch(id -> id.equals(guarantorId));
            if (isNotMatchGuarantor) {
                UserSkillGuarantee newUserSkillGuarantee = UserSkillGuarantee.builder().user(receiver)
                        .skill(skill)
                        .guarantor(guarantor)
                        .build();
                skill.getGuarantees().add(newUserSkillGuarantee);
                userSkillGuaranteeRepository.save(newUserSkillGuarantee);
                skillRepository.save(skill);
            }
        }
        return existingSkill.stream()
                .filter(id -> matchingSkills.stream().noneMatch(existing -> existing.getId() == id))
                .toList();
    }

    private void clearingSkills(RecommendationDto recommendation) {
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        for (SkillOfferDto skillOffers : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOffers.getSkillId(), recommendation.getId());
        }
        guaranteedSkills(recommendation);
    }
}
