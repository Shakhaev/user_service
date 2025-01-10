package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final static int PERIOD_TO_ADD_NEW_RECOMMENDATION = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendationForPeriod(recommendation);
        validateSkillsInSystem(recommendation);

        List<Long> skillIds = getSkillIds(recommendation);
        Long recommendationId = recommendationRepository
                .create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        saveSkillOffers(recommendationId, skillIds);

        return recommendation;
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        validateRecommendationForPeriod(recommendation);
        validateSkillsInSystem(recommendation);

        recommendationRepository
                .update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        List<Long> skillIds = getSkillIds(recommendation);
        for (Long skillId : skillIds) {
            skillOfferRepository.create(skillId, recommendation.getId());
        }

        return recommendation;
    }

    public void delete(Long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> allByReceiverId = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());
        List<Recommendation> recommendations = allByReceiverId.getContent();

        return recommendationMapper.mapToDtoList(recommendations);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> allByAuthorId = recommendationRepository
                .findAllByAuthorId(authorId, Pageable.unpaged());
        List<Recommendation> recommendations = allByAuthorId.getContent();

        return recommendationMapper.mapToDtoList(recommendations);
    }

    private void validateRecommendationForPeriod(RecommendationDto recommendation) {
        Optional<LocalDateTime> lastRecommendationDate;
        lastRecommendationDate = getLastRecommendationDate(recommendation.getAuthorId(), recommendation.getReceiverId());
        lastRecommendationDate.ifPresent(date -> {
            if (date.plusMonths(PERIOD_TO_ADD_NEW_RECOMMENDATION).isAfter(LocalDateTime.now())) {
                throw new BusinessException(
                        String.format("Новая рекомендация может быть дана не ранее, чем через %s месяцев",
                                PERIOD_TO_ADD_NEW_RECOMMENDATION));
            }
        });
    }

    private void validateSkillsInSystem(RecommendationDto recommendation) {
        List<Long> skillIds = getSkillIds(recommendation);
        List<Skill> skillsFromDb = skillRepository.findAllById(skillIds);

        if (skillsFromDb.size() != skillIds.size()) {
            throw new DataValidationException("Вы предлагаете навыки, которых нет в системе");
        }
    }

    private void saveSkillOffers(Long recommendationId, List<Long> skillIds) {
        for (Long skillId : skillIds) {
            skillOfferRepository.create(skillId, recommendationId);
        }
    }

    private List<Long> getSkillIds(RecommendationDto recommendation) {
        return recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
    }

    private Optional<LocalDateTime> getLastRecommendationDate(Long authorId, Long receiverId) {
        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
        if (lastRecommendation.isPresent()) {
            LocalDateTime date = lastRecommendation.get().getCreatedAt();
            return Optional.ofNullable(date);
        } else {
            return Optional.empty();
        }
    }
}
