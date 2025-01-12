package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        validateOfLatestRecommendation(recommendation);
        validateOfSkills(recommendation.getSkillOffers());
        saveSkillOffers(recommendation);
        recommendationRepository.create(recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        return recommendation;
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        validateOfLatestRecommendation(recommendation);
        validateOfSkills(recommendation.getSkillOffers());
        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(),
                recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        return saveSkillOffers(recommendation);
    }

    public void delete(long id){
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId){
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, Pageable.unpaged());

        return recommendations.stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation))
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId){
        Page<Recommendation> recommendations = recommendationRepository.findAllByAuthorId(authorId, Pageable.unpaged());
        return recommendations.stream()
                .map(recommendation -> recommendationMapper.toDto(recommendation))
                .toList();
    }

    RecommendationDto saveSkillOffers(RecommendationDto recommendation) {
        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOfferDto.getId(), recommendation.getId());
        }
        return recommendation;

    }

    void validateOfSkills(List<SkillOfferDto> skills) {
        List<Long> skillIds = skills.stream()
                .map(skill -> skill.getId())
                .toList();
        if (skills.size() != skillRepository.countExisting(skillIds)) {
            throw new IllegalArgumentException("Некоторые навыки не найдены в системе");
        }

    }

    void validateOfLatestRecommendation(RecommendationDto recommendation) {
        LocalDateTime dateNow = recommendation.getCreatedAt();
        LocalDateTime lastRecommendationDate = recommendationRepository.
                findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(),
                        recommendation.getReceiverId()
                ).map(Recommendation::getCreatedAt)
                .orElse(null);
        if (lastRecommendationDate != null && ChronoUnit.MONTHS.between(dateNow, lastRecommendationDate) <= 6) {
            throw new IllegalArgumentException("Вы давали рекомендацию пользователю");
        }
    }


}

