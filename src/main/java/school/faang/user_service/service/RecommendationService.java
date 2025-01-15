package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository guaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private static final int VALID_MONTH = 6;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        Long createdRecommendationId = recommendationRepository.create(recommendation.getAuthorId(),
                recommendation.getReceiverId(), recommendation.getContent());
        createSkillOfersByExistSkills(recommendation, createdRecommendationId);
        return recommendation;
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        if (recommendation.getId() == null) {
            throw new DataValidationException("RecommendationId cannot be null!");
        }
        validateRecommendation(recommendation);
        recommendationRepository.update(recommendation.getAuthorId(),
                recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        createSkillOfersByExistSkills(recommendation, recommendation.getId());
        return recommendation;
    }

    public void delete(long id) {
        if (id <= 0) {
            throw new DataValidationException("The recommendation ID cannot be less than or equal to zero!");
        }
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        if (recommendations == null || recommendations.isEmpty()) {
            return null;
        }
        return recommendations.map(recommendation -> recommendationMapper.toDto(recommendation));
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);
        if (recommendations == null || recommendations.isEmpty()) {
            return null;
        }
        return recommendations.map(recommendation -> recommendationMapper.toDto(recommendation));
    }


    private void createSkillOfersByExistSkills(RecommendationDto recommendation, Long createdRecommendationId) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();
        if (skillOffers != null) {
            List<SkillOfferDto> existSkills = existSkillsInSystem(skillOffers);
            validateSkills(existSkills);
            recommendation.setSkillOffers(existSkills);
            recommendation.setId(createdRecommendationId);
            createOffers(recommendation);
        }
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (!isValidRecommendation(recommendation)) {
            throw new DataValidationException("You have already recommended this user");
        }
    }

    private void validateSkills(List<SkillOfferDto> existSkills) {
        if (existSkills == null || existSkills.isEmpty()) {
            throw new DataValidationException("These skills do not exist in the system");
        }
    }

    private boolean isValidRecommendation(RecommendationDto recommendation) {
        return recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthorId(), recommendation.getReceiverId())
                .map(lastRecommendation -> isRecommendationValid(lastRecommendation, recommendation))
                .orElse(true);
    }

    private boolean isRecommendationValid(Recommendation lastRecommendation, RecommendationDto recommendation) {
        LocalDateTime createdAt = lastRecommendation.getCreatedAt();
        long monthsBetween = ChronoUnit.MONTHS.between(createdAt, recommendation.getCreatedAt());
        return monthsBetween >= VALID_MONTH;
    }

    private List<SkillOfferDto> existSkillsInSystem(List<SkillOfferDto> skillOffers) {
        List<Skill> skills = skillRepository.findAll();
        if (skills == null) {
            return Collections.emptyList();
        }
        List<Long> allSkillIds = skills.stream().map(Skill::getId).collect(Collectors.toList());
        return skillOffers.stream().filter(skill -> allSkillIds.contains(skill.getSkillId()))
                .collect(Collectors.toList());
    }

    private void createOffers(RecommendationDto recommendation) {
        Long receiverId = recommendation.getReceiverId();
        Long userId = recommendation.getAuthorId();
        Long recommendationId = recommendation.getId();
        recommendation.getSkillOffers().forEach(skillOffer -> {
            List<SkillOffer> findOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillOffer.getSkillId(), receiverId);
            if (isNotEmpty(findOffersOfSkill)) {
                if (!isGuaranteeExist(userId, receiverId, skillOffer.getSkillId())) {
                    guaranteeRepository.create(receiverId, skillOffer.getSkillId(), userId);
                }
            } else {
                skillOfferRepository.create(skillOffer.getSkillId(), recommendationId);
            }
        });
    }

    private boolean isNotEmpty(List<SkillOffer> findOffersOfSkill) {
        return findOffersOfSkill != null && !findOffersOfSkill.isEmpty();
    }

    private boolean isGuaranteeExist(Long userId, Long receiverId, Long skillId) {
        Iterable<UserSkillGuarantee> guarantees = guaranteeRepository.findAll();
        if (guarantees == null) {
            return false;
        }
        return StreamSupport.stream(guarantees.spliterator(), false)
                .anyMatch(guarantee -> userId.equals(guarantee.getUser().getId())
                        && receiverId.equals(guarantee.getGuarantor().getId())
                        && skillId.equals(guarantee.getSkill().getId()));
    }
}
